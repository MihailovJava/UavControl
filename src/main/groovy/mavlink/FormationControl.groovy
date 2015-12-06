package mavlink

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import genetic.entity.FormationEntity
import genetic.entity.InitPositionEntity
import genetic.genetic.squad.SquadGene
import genetic.genetic.system.Chromosome
import genetic.repository.FormationRepository
import genetic.repository.InitPositionRepository
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.lang.reflect.Type


class FormationControl extends Observable implements Runnable {

    public static final float MIN_DIST = 0.5

    PixHawkMavLinkService service

    @Autowired
    FormationRepository formationRepository

    @Autowired
    InitPositionRepository initPositionRepository

    int positionInFormation

    int formationId

    def interval
    def distance

    def di
    def dj

    @Autowired
    Gson gson

    List<InitPositionEntity> initFormation
    MavLinkPosition currentTarget


    void afterInit() {
        new Thread({
            service.prepareToFlight(positionInFormation)
            Thread.sleep(60_000)
            def count = service.getCount()
            initFormation = initPositionRepository.findByNumber(count)

            InitPositionEntity entity = initFormation.get(formationId)
            if (entity != null) {

                interval = entity.interval
                distance = entity.distance
                Type type = new TypeToken<ArrayList<SquadGene>>() {}.type
                List<SquadGene> position = gson.fromJson(entity.jsonPosition, type)
                MavLinkPosition leaderPosition = service.getCurrentPosition(0) // 0 0 -14?
                SquadGene leaderFormationPosition = position.get(0)

                SquadGene uavFormationPosition = position.get(positionInFormation)
                di = uavFormationPosition.i - leaderFormationPosition.i
                dj = uavFormationPosition.j - leaderFormationPosition.j
                MavLinkPosition dp = new MavLinkPosition(nedX: di * distance, nedY: dj * interval, nedZ: 0)
                currentTarget = leaderPosition + dp
                service.flightHere(currentTarget, positionInFormation)

            }
        },"CONTROL " + System.currentTimeMillis()).start()


    }


    @Override
    void run() {

    }

    void flightHere(MavLinkPosition position) {
        MavLinkPosition dp = new MavLinkPosition(nedX: di * distance, nedY: dj * interval, nedZ: 0)
        currentTarget = position + dp
        service.flightHere(currentTarget, positionInFormation)
    }

    void checkMyPosition(MavLinkPosition currentPosition, int id) {
        if (currentTarget != null) {
            def dist = (currentTarget - currentPosition).length()
            if (dist < MIN_DIST) {
                setChanged()
                notifyObservers("TARGET_REACHED")
                currentTarget = null
            }
        }
    }

    void changeFormationTo(int formationId) {
        if (formationId == this.formationId) {
            String from = initFormation.get(this.formationId)
            String to = initFormation.get(formationId)
            FormationEntity change = formationRepository.findByFromFormationAndToFormation(from, to)
            Type type = new TypeToken<Chromosome<SquadGene>>() {}.type
            Chromosome<SquadGene> chromosome = gson.fromJson(change.jsonArray, type)
            SquadGene toPosition = chromosome.getGene(positionInFormation)

            type = new TypeToken<ArrayList<SquadGene>>() {}.type
            List<SquadGene> fromPositionList = gson.fromJson(initFormation.get(this.formationId).jsonPosition, type)
            SquadGene fromPosition = fromPositionList.get(positionInFormation)

            di = toPosition.i - fromPosition.i
            dj = toPosition.j - fromPosition.j

            MavLinkPosition dp = new MavLinkPosition(nedX: di * distance, nedY: dj * interval, nedZ: 0);
            MavLinkPosition currentPosition = service.getCurrentPosition(positionInFormation)
            currentTarget = currentPosition + dp
            service.flightHere(currentTarget, positionInFormation)
        }
    }
}

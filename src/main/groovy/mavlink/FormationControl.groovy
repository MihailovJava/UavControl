package mavlink

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import genetic.entity.FormationEntity
import genetic.entity.InitPositionEntity
import genetic.genetic.squad.SquadGene
import genetic.genetic.system.Chromosome
import genetic.repository.FormationRepository
import genetic.repository.InitPositionRepository
import math.Quaternion
import mavlink.control.commands.Command
import org.springframework.beans.factory.annotation.Autowired

import java.lang.reflect.Type
import java.util.concurrent.CyclicBarrier

class FormationControl extends Observable {

    public static final float MIN_DIST = 0.5

    PixHawkMavLinkService service

    @Autowired
    FormationRepository formationRepository

    @Autowired
    InitPositionRepository initPositionRepository

    Deque<Command> commandDeque

    def deltaPos
    def deltaYaw

    int positionInFormation

    int formationId

    def interval
    def distance

    def di
    def dj

    def diY
    def djY

    def icm
    def jcm

    float formationYaw = Math.PI / 4

    @Autowired
    Gson gson


    List<InitPositionEntity> initFormation
    MavLinkPosition currentTarget
    MavLinkPosition currentPosition
    MavLinkOrientation currentOrientation
    MavLinkPosition missionTarget

    void doInitFormation() {
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

            di = uavFormationPosition.i //- leaderFormationPosition.i
            dj = uavFormationPosition.j //- leaderFormationPosition.j

            icm = 0
            jcm = 0

            for (int i = 0; i < count; i++) {
                icm += position.get(i).i
                jcm += position.get(i).j
            }

            icm /= count
            jcm /= count

            djY = (di - icm) * Math.cos(formationYaw) + (dj - jcm) * -Math.sin(formationYaw)
            diY = (di - icm) * Math.sin(formationYaw) + (dj - jcm) * Math.cos(formationYaw)

            MavLinkPosition dp = new MavLinkPosition(nedX: diY * distance, nedY: djY * interval, nedZ: 0)
            currentTarget = leaderPosition + dp
        }
    }

    void doRotateFormation(){

        float xcm = 0
        float ycm = 0

        def count = service.getCount()
        for (int i = 0; i < count; i++) {
            def currentPosition = service.getCurrentPosition(i)
            xcm += currentPosition.nedX
            ycm += currentPosition.nedY
        }
        xcm /= count
        ycm /= count
        float targetYaw = Math.atan2(missionTarget.nedY - ycm, missionTarget.nedX - xcm)

        djY = di * Math.cos(targetYaw) + dj * -Math.sin(targetYaw)
        diY = di * Math.sin(targetYaw) + dj * Math.cos(targetYaw)
        formationYaw = targetYaw

        MavLinkPosition dp = new MavLinkPosition(nedX: diY * distance, nedY: djY * interval, nedZ: 0)
        //1 �.�.

        //2 ����
        //3 ��������
        //4 �������� ���������
        //5 ������� ���������
        currentTarget = currentPosition + dp

    }

    void doFlightHere(){
        MavLinkPosition dp = new MavLinkPosition(nedX: diY * distance, nedY: djY * interval, nedZ: 0)
        currentTarget = missionTarget + dp
    }

    void afterInit() {
        new Thread({
            while (true) {
                def command = commandDeque.poll()
                if (command != null) {
                    command.execute(this)
                    def flag = true
                    println(positionInFormation + " START " + command)
                    while (command.latch.getCount() > 0) {
                        def condition
                        if (command.condition.getType() == MavLinkPosition.class) {
                            condition = currentPosition
                        } else if (command.condition.getType() == MavLinkOrientation.class) {
                            condition = currentOrientation
                        }

                        if (condition != null && command.condition.isOk(condition) && flag) {
                            command.latch.countDown()
                            println(positionInFormation + " DONE " + command)
                            //  command.latch.await()
                            flag = false
                        }
                        //
                        Thread.sleep(10)
                    }

                    println(positionInFormation + " SLEEP " + command.latch.count)
                    Thread.sleep(100)
                    command.latch.set()
                    println(positionInFormation + " RESET " + command.latch.count)


                }
                Thread.sleep(10)
            }
        }, "CONTROL " + System.currentTimeMillis()).start()
    }




    void checkMyPosition(MavLinkPosition currentPosition, int id) {
       this.currentPosition = currentPosition
    }

    void checkMyOrientation(MavLinkOrientation orientation,int id){
        this.currentOrientation = orientation
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

            icm = 0
            jcm = 0

            def count = chromosome.size
            for (int i = 0; i < count; i++) {
                icm += chromosome.getGene(i).i
                jcm += chromosome.getGene(i).j
            }

            icm /= count
            jcm /= count

            djY = (di - icm) * Math.cos(formationYaw) + (dj - jcm) * -Math.sin(formationYaw)
            diY = (di - icm) * Math.sin(formationYaw) + (dj - jcm) * Math.cos(formationYaw)

            MavLinkPosition dp = new MavLinkPosition(nedX: diY * distance, nedY: djY * interval, nedZ: 0);
            MavLinkPosition currentPosition = service.getCurrentPosition(positionInFormation)
            currentTarget = currentPosition + dp
            service.flightHere(currentTarget, positionInFormation)
        }
    }
}

package mavlink

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import hungarian.SquadPosition
import hungarian.entity.FormationEntity
import hungarian.entity.InitPositionEntity
import hungarian.repository.FormationRepository
import hungarian.repository.InitPositionRepository
import mavlink.control.commands.Command
import org.springframework.beans.factory.annotation.Autowired

import java.lang.reflect.Type

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

    int positionInList
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

    def xcm
    def ycm

    float formationYaw

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
            Type type = new TypeToken<ArrayList<SquadPosition>>() {}.type
            List<SquadPosition> position = gson.fromJson(entity.jsonPosition, type)
            MavLinkPosition leaderPosition = service.getCurrentPosition(0) // 0 0 -14?

            xcm = 0;
            ycm = 0;

            for (int i = 0; i < count; i++) {
                def currentPosition = service.getCurrentPosition(i)
                xcm += currentPosition.nedX
                ycm += currentPosition.nedY
            }
            xcm /= count
            ycm /= count

            SquadPosition uavFormationPosition = position.get(positionInFormation)

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

            djY = (dj - jcm) * Math.cos(formationYaw) + (di - icm) * -Math.sin(formationYaw)
            diY = (dj - jcm) * Math.sin(formationYaw) + (di - icm) * Math.cos(formationYaw)

            djY *= interval
            diY *= distance

            MavLinkPosition dp = new MavLinkPosition(nedX: djY, nedY: diY, nedZ: 0)
            currentTarget = new MavLinkPosition(nedX: xcm, nedY: ycm, nedZ: leaderPosition.nedZ) + dp
        }
    }

    void doRotateFormation() {

        xcm = 0
        ycm = 0

        def count = service.getCount()
        for (int i = 0; i < count; i++) {
            def currentPosition = service.getCurrentPosition(i)
            xcm += currentPosition.nedX
            ycm += currentPosition.nedY
        }
        xcm /= count
        ycm /= count
        float targetYaw = Math.atan2(missionTarget.nedY - ycm, missionTarget.nedX - xcm)

        djY = (dj - jcm) * Math.cos(targetYaw) + (di - icm) * -Math.sin(targetYaw)
        diY = (dj - jcm) * Math.sin(targetYaw) + (di - icm) * Math.cos(targetYaw)

        djY *= interval
        diY *= distance

        formationYaw = targetYaw

        MavLinkPosition dp = new MavLinkPosition(nedX: djY, nedY: diY, nedZ: 0)
        //1 �.�.

        //2 ����
        //3 ��������
        //4 �������� ���������
        //5 ������� ���������
        currentTarget = new MavLinkPosition(nedX: xcm, nedY: ycm, nedZ: currentPosition.nedZ) + dp
        println "index $positionInFormation jcm = $jcm icm = $icm yaw = $targetYaw djY = $djY diY = $diY\n" +
                "xcm = $xcm ycm = $ycm  tx = $currentTarget.nedX ty = $currentTarget.nedY"


    }

    void doFlightHere() {
        MavLinkPosition dp = new MavLinkPosition(nedX: djY, nedY: diY, nedZ: 0)
        currentTarget = missionTarget + dp
    }

    void afterInit() {
        new Thread({
            while (true) {
                def command = commandDeque.poll()
                if (command != null) {
                    command.execute(this)
                    def flag = true
                    println(positionInList + " START " + command)
                    while (command.latch.getCount() > 0) {
                        def condition
                        if (command.condition.getType() == MavLinkPosition.class) {
                            condition = currentPosition
                        } else if (command.condition.getType() == MavLinkOrientation.class) {
                            condition = currentOrientation
                        }

                        if (condition != null && command.condition.isOk(condition) && flag) {
                            command.latch.countDown()
                            println(positionInList + " DONE " + command)
                            //  command.latch.await()
                            flag = false
                        }
                        //
                        Thread.sleep(10)
                    }

                    println(positionInList + " SLEEP " + command.latch.count)
                    Thread.sleep(100)
                    command.latch.set()
                    println(positionInList + " RESET " + command.latch.count)


                }
                Thread.sleep(1000)
            }
        }, "CONTROL " + System.currentTimeMillis()).start()
    }


    void checkMyPosition(MavLinkPosition currentPosition, int id) {
        this.currentPosition = currentPosition
    }

    void checkMyOrientation(MavLinkOrientation orientation, int id) {
        this.currentOrientation = orientation
    }

    void changeFormationTo(int formationId) {
        if (formationId != this.formationId) {
            String from = initFormation.get(this.formationId).id
            String to = initFormation.get(formationId).id
            FormationEntity change = formationRepository.findByFromFormationAndToFormation(from, to)
            Type type = new TypeToken<List<SquadPosition>>() {}.type
            List<SquadPosition> list = gson.fromJson(change.jsonArray, type)
            SquadPosition toPosition = list.get(positionInFormation)

            type = new TypeToken<ArrayList<SquadPosition>>() {}.type
            List<SquadPosition> fromPositionList = gson.fromJson(initFormation.get(this.formationId).jsonPosition, type)
            SquadPosition fromPosition = fromPositionList.get(positionInFormation)
            positionInFormation = toPosition.pos-1
            this.formationId = formationId

            di = toPosition.i - fromPosition.i
            dj = toPosition.j - fromPosition.j

            xcm = 0
            ycm = 0
            icm = 0
            jcm = 0

            def count = list.size()
            for (int i = 0; i < count; i++) {
                icm += list.get(i).i
                jcm += list.get(i).j
                def currentPosition = service.getCurrentPosition(i)
                xcm += currentPosition.nedX
                ycm += currentPosition.nedY
            }

            icm /= count
            jcm /= count
            xcm /= count
            ycm /= count

            djY = (dj ) * Math.cos(formationYaw) + (di ) * -Math.sin(formationYaw)
            diY = (dj ) * Math.sin(formationYaw) + (di ) * Math.cos(formationYaw)

            djY *= interval
            diY *= distance

            di = toPosition.i
            dj = toPosition.j

            MavLinkPosition dp = new MavLinkPosition(nedX: djY, nedY: diY, nedZ: 0);
            MavLinkPosition currentPosition = service.getCurrentPosition(positionInList)
        //    def currentPositionCM = currentPosition - new MavLinkPosition(nedX: xcm,nedY: ycm, nedZ: currentPosition.nedZ)
          //  def d = dp - currentPositionCM
            currentTarget = currentPosition + dp // new MavLinkPosition(nedX: xcm, nedY: ycm, nedZ: currentPosition.nedZ) + dp

        }
    }
}

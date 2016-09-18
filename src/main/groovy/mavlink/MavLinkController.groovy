package mavlink

import mavlink.control.commands.ChangeFormationCommand
import mavlink.control.commands.FlightToFormationCommand
import mavlink.control.commands.FlightToPointCommand
import mavlink.control.commands.RotateAircraftCommand
import mavlink.control.commands.RotateFormationCommand
import mavlink.control.commands.TakeOffCommand
import mavlink.control.conditions.FlightToPointCondition
import mavlink.control.conditions.RotateAircraftCondition
import mavlink.util.ResettableCountDownLatch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.CountDownLatch


class MavLinkController implements Observer {

    List<MavAircraft> connectionList

    List<MavLinkPosition> missionList

    @Autowired
    Environment environment


    File[] files

    @PostConstruct
    void afterInit() {
        files = [
                new File(environment.getProperty('mavlink.control.file.name.position')),
                new File(environment.getProperty('mavlink.control.file.name.orientation'))]
        files.each { it.withWriter { w -> w.write("") } }
        def latch = new ResettableCountDownLatch(connectionList.size())
        connectionList.each {
            it.formationControl.commandDeque.add(new TakeOffCommand(latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new FlightToFormationCommand(latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 2,latch: latch))
        }
        def toRadians = Math.PI/180;
        connectionList.each {
            for (int i = 0; i < 6; i++) {
                def angle = (i + 1) * 15 * toRadians
                it.formationControl.commandDeque.add(new RotateFormationCommand(
                        latch: latch,
                        condition: new FlightToPointCondition(target:new MavLinkPosition(nedX:  15*Math.cos(angle),nedY: 15*Math.sin(angle)))))
            }
        }

        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 0,latch: latch))
        }
        connectionList.each {
            for (int i = 6; i < 12; i++) {
                def angle = (i + 1) * 15 * toRadians
                it.formationControl.commandDeque.add(new RotateFormationCommand(
                        latch: latch,
                        condition: new FlightToPointCondition(target:new MavLinkPosition(nedX:  15*Math.cos(angle),nedY: 15*Math.sin(angle)))))
            }

        }
        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 1,latch: latch))
        }
        connectionList.each {
            for (int i = 12; i < 18; i++) {
                def angle = (i + 1) * 15 * toRadians
                it.formationControl.commandDeque.add(new RotateFormationCommand(
                        latch: latch,
                        condition: new FlightToPointCondition(target:new MavLinkPosition(nedX:  15*Math.cos(angle),nedY: 15*Math.sin(angle)))))
            }

        }
        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 3,latch: latch))
        }
        connectionList.each {
            for (int i = 18; i < 24; i++) {
                def angle = (i + 1) * 15 * toRadians
                it.formationControl.commandDeque.add(new RotateFormationCommand(
                        latch: latch,
                        condition: new FlightToPointCondition(target:new MavLinkPosition(nedX:  15*Math.cos(angle),nedY: 15*Math.sin(angle)))))
            }

        }
    /*    connectionList.each {
            it.formationControl.commandDeque.add(new FlightToPointCommand(
                    condition: new FlightToPointCondition(target: new MavLinkPosition(nedX: 0, nedY:  130, nedZ:-14 )),
                    latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 2,latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new FlightToPointCommand(
                    condition: new FlightToPointCondition(target: new MavLinkPosition(nedX: 130, nedY:  130, nedZ:-14 )),
                    latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new ChangeFormationCommand(formationId: 0,latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new FlightToPointCommand(
                    condition: new FlightToPointCondition(target: new MavLinkPosition(nedX: 130, nedY:  0, nedZ:-14 )),
                    latch: latch))
        }*/
      /*  def toRadians = Math.PI/360
        connectionList.each {
            for (int i = 0; i < 6; i++) {
                it.formationControl.commandDeque.add(new RotateAircraftCommand(latch: latch,
                        condition: new RotateAircraftCondition(yaw: (i + 1) * 15 * toRadians, deltaYaw: it.formationControl.deltaYaw)))
                it.formationControl.commandDeque.add(new RotateAircraftCommand(latch: latch,
                        condition: new RotateAircraftCondition(yaw: 0, deltaYaw: it.formationControl.deltaYaw)))
            }

        }*/
    }

    @Override
    void update(Observable o, Object arg) {
        List<Object> args = arg as ArrayList
        if (args.get(0) instanceof MavLinkOrientation ) {
            MavLinkOrientation orientation = args[0] as  MavLinkOrientation
            files[1] << ([orientation, args[1]].toString() + '\n')
        }
        if(args.get(0) instanceof MavLinkPosition ){
            MavLinkPosition position = args[0] as  MavLinkPosition
            files[0] << ([position, args[1]].toString() + '\n')
        }

    }

    @PreDestroy
    void beforeDestroy() {
        files.each { it.deleteOnExit() }
    }
}

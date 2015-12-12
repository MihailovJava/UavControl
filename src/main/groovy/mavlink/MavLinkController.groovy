package mavlink

import mavlink.control.commands.FlightToFormationCommand
import mavlink.control.commands.FlightToPointCommand
import mavlink.control.commands.TakeOffCommand
import mavlink.control.conditions.FlightToPointCondition
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
        files.each {it.withWriter { w -> w.write("") }}
        def latch = new ResettableCountDownLatch(connectionList.size())
        connectionList.each {
            it.formationControl.commandDeque.add(new TakeOffCommand(latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new FlightToFormationCommand(latch: latch))
        }
        connectionList.each {
            it.formationControl.commandDeque.add(new FlightToPointCommand(
                    condition: new FlightToPointCondition(target: new MavLinkPosition(nedX: 30, nedY:  30, nedZ:-14 )),
                    latch: latch))
        }
    }

    @Override
    void update(Observable o, Object arg) {
        List<Object> args = arg as ArrayList
        if( args.get(0) instanceof MavAircraft ) {
            MavAircraft aircraft = args[0] as MavAircraft
            files[0] << ([aircraft.currentPosition, args[1]].toString() + '\n')
            files[1] << ([aircraft.currentOrientation, args[1]].toString() + '\n')
        }

    }

    @PreDestroy
    void beforeDestroy() {
        files.each {it.deleteOnExit()}
    }
}

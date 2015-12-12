package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.FlightToPointCondition

import java.util.concurrent.CountDownLatch


class FlightToPointCommand extends Command{

    @Override
    void execute(FormationControl control) {
        control.missionTarget = (condition as FlightToPointCondition).target
        (condition as FlightToPointCondition).delta = control.deltaPos
        control.commandDeque.addFirst(new FlightCommand(latch: latch, condition: condition))
        control.commandDeque.addFirst(new RotateAircraftCommand(latch: latch))
        control.commandDeque.addFirst(new RotateFormationCommand(latch: latch))
        latch.countDown()
    }
}

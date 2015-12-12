package mavlink.control.commands

import mavlink.FormationControl
import mavlink.MavLinkPosition
import mavlink.control.commands.Command
import mavlink.control.conditions.FlightToPointCondition


class FlightCommand extends Command {

    @Override
    void execute(FormationControl control) {
        control.doFlightHere()
        (condition as FlightToPointCondition).target = control.currentTarget
        control.service.flightHere(control.currentTarget,control.positionInFormation)
    }
}

package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.FlightToPointCondition

class FlightToFormationCommand extends Command {


    @Override
    void execute(FormationControl control) {
        control.doInitFormation()
        condition = new FlightToPointCondition(target: control.currentTarget,delta: control.deltaPos)
        control.service.flightHere(control.currentTarget, control.positionInFormation)
    }
}

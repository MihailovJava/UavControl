package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.FlightToPointCondition


class RotateFormationCommand extends Command {

    @Override
    void execute(FormationControl control) {
        control.doRotateFormation()
        condition = new FlightToPointCondition(delta: control.deltaPos,target: control.currentTarget)
        control.service.flightHere(control.currentTarget, control.positionInFormation)
    }
}

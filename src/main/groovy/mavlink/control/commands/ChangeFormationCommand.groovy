package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.FlightToPointCondition


class ChangeFormationCommand extends Command {
    int formationId

    @Override
    void execute(FormationControl control) {
        control.changeFormationTo(formationId)
        condition = new FlightToPointCondition(target: control.currentTarget,delta: control.deltaPos)
        control.service.flightHere(control.currentTarget, control.positionInList)
    }
}

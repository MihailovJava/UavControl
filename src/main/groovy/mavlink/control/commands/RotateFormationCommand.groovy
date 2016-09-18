package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.FlightToPointCondition


class RotateFormationCommand extends Command {

    @Override
    void execute(FormationControl control) {
        if(condition != null)
            control.missionTarget = (condition as FlightToPointCondition).target;
        control.doRotateFormation()
        condition = new FlightToPointCondition(delta: control.deltaPos,target: control.currentTarget)
        control.service.flightHere(control.currentTarget, control.positionInList)
    }
}

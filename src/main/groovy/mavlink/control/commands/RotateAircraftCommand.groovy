package mavlink.control.commands

import math.Quaternion
import mavlink.FormationControl
import mavlink.control.conditions.RotateAircraftCondition


class RotateAircraftCommand extends Command{

    @Override
    void execute(FormationControl control) {
        condition = new RotateAircraftCondition(yaw: control.formationYaw,deltaYaw: control.deltaYaw)
        control.service.changeOrientation(new Quaternion(control.formationYaw,0,0),control.positionInFormation)
    }
}

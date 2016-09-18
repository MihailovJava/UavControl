package mavlink.control.commands

import math.Quaternion
import mavlink.FormationControl
import mavlink.control.conditions.RotateAircraftCondition


class RotateAircraftCommand extends Command{

    @Override
    void execute(FormationControl control) {
        if (condition == null) {
            condition = new RotateAircraftCondition(yaw: control.formationYaw, deltaYaw: control.deltaYaw)
            control.service.changeOrientation(new Quaternion(control.formationYaw, 0, 0), control.positionInList)
        } else {
            control.service.changeOrientation(new Quaternion((condition as RotateAircraftCondition ).yaw, 0, 0), control.positionInList)
        }

    }
}

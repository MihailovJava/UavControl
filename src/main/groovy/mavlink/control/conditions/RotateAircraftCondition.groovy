package mavlink.control.conditions

import mavlink.MavLinkOrientation


class RotateAircraftCondition implements Condition<MavLinkOrientation> {

    float yaw
    float deltaYaw

    @Override
    boolean isOk(MavLinkOrientation mavLinkOrientation) {
        return Math.abs(yaw - mavLinkOrientation.psi) < deltaYaw
    }

    @Override
    Class<MavLinkOrientation> getType() {
        return MavLinkOrientation.class
    }
}

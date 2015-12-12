package mavlink.control.conditions

import mavlink.MavLinkPosition


class FlightToPointCondition implements Condition<MavLinkPosition>{

    MavLinkPosition target
    float delta

    @Override
    boolean isOk(MavLinkPosition mavLinkPosition) {
        def d = target - mavLinkPosition
        d.nedZ = 0
        def dist = d.length()
        return dist < delta
    }

    @Override
    Class<MavLinkPosition> getType() {
        return MavLinkPosition.class
    }
}

package mavlink.control.conditions

import mavlink.MavLinkPosition


class TakeOffCondition implements Condition<MavLinkPosition>{

    float height
    float delta

    @Override
    boolean isOk(MavLinkPosition mavLinkPosition) {
        return Math.abs(height-mavLinkPosition.nedZ) < delta
    }

    @Override
    Class<MavLinkPosition> getType() {
        return MavLinkPosition.class
    }
}

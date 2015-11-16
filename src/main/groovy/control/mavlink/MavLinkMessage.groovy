package control.mavlink


enum MavLinkMessage {

    MAV_CMD_COMPONENT_ARM_DISARM ('Arms / Disarms a component : \nMission Param #1\t1 to arm, 0 to disarm',400),
    MAV_CMD_DO_SET_MODE ('Set system mode : \n Mission Param #1\tMode, as defined by ENUM MAV_MODE',176),
    MAV_CMD_DO_CHANGE_SPEED('Change speed and/or throttle set points. :' +
            '\n\tMission Param #1\tSpeed type (0=Airspeed, 1=Ground Speed)' +
            '\n\tMission Param #2\tSpeed (m/s, -1 indicates no change)' +
            '\n\tMission Param #3\tThrottle ( Percent, -1 indicates no change)',178),
    MAV_CMD_NAV_WAYPOINT('Navigate to MISSION : ' +
            '\nMission Param #1\tHold time in decimal seconds. (ignored by fixed wing, time to stay at MISSION for rotary wing)\n' +
            'Mission Param #2\tAcceptance radius in meters (if the sphere with this radius is hit, the MISSION counts as reached)\n' +
            'Mission Param #3\t0 to pass through the WP, if > 0 radius in meters to pass by WP. Positive value for clockwise orbit, negative value for counter-clockwise orbit. Allows trajectory control.\n' +
            'Mission Param #4\tDesired yaw angle at MISSION (rotary wing)\n' +
            'Mission Param #5\tLatitude\n' +
            'Mission Param #6\tLongitude\n' +
            'Mission Param #7\tAltitude',16),
    MAV_CMD_NAV_TAKEOFF('Takeoff from ground / hand : \n' +
            'Mission Param #1\tMinimum pitch (if airspeed sensor present), desired pitch without sensor\n' +
            'Mission Param #2\tEmpty\n' +
            'Mission Param #3\tEmpty\n' +
            'Mission Param #4\tYaw angle (if magnetometer present), ignored without magnetometer\n' +
            'Mission Param #5\tLatitude\n' +
            'Mission Param #6\tLongitude\n' +
            'Mission Param #7\tAltitude',22),
    HEARTBEAT('type\tuint8_t\tType of the MAV (quadrotor, helicopter, etc., up to 15 types, defined in MAV_TYPE ENUM)\n' +
            'autopilot\tuint8_t\tAutopilot type / class. defined in MAV_AUTOPILOT ENUM\n' +
            'base_mode\tuint8_t\tSystem mode bitfield, see MAV_MODE_FLAG ENUM in mavlink/include/mavlink_types.h\n' +
            'custom_mode\tuint32_t\tA bitfield for use for autopilot-specific flags.\n' +
            'system_status\tuint8_t\tSystem status flag, see MAV_STATE ENUM\n' +
            'mavlink_version\tuint8_t_mavlink_version\tMAVLink version, not writable by user, gets added by protocol because of magic data type: uint8_t_mavlink_version'
            ,0),
    PARAM_REQUEST_LIST('target_system\tuint8_t\tSystem ID\n' +
            'target_component\tuint8_t\tComponent ID',21);
    int id
    String description
    Object[] params = new Object[7]


    MavLinkMessage(String description,int id){
        this.id = id
        this.description = description
    }

    MavLinkMessage setParams(Object ... params){
        for (int i = 0; i < params.length ; i++){
            this.params[i] = params[i]
        }
        return this
    }
}
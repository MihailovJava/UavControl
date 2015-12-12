package mavlink

import math.Quaternion

class PixHawkMavLinkService implements MavLinkService {


    List<MavAircraft> connectionList

    @Override
    void flightHere(MavLinkPosition target, int id) {
        def msg = new MavLink.MSG_SET_POSITION_TARGET_LOCAL_NED(
                MavLink.MAV_SYSTEM_ID, MavLink.MAV_COMPONENT_ID, 0L,
                target.nedX, target.nedY, target.nedZ,
                0, 0, 0,
                0, 0, 0,
                0, 0,
              //  0b0_000_111_111_1_11_000, // type_mask (only speeds enabled)
                 0b0_111_000_111_1_11_000, // type_mask (only positions enabled)
                1, 1, MavLink.MAV_FRAME_LOCAL_NED)
        msg = MavLink.Message.decodeMessage(msg.revert().encode())
        connectionList.get(id).task.addTask(msg,20L)
    }



    @Override
    MavLinkPosition getCurrentPosition(int id) {
        def position = connectionList.get(id).currentPosition
        return position
    }

    @Override
    int getCount() {
        return connectionList.size()
    }

    @Override
    void prepareToFlight(int id) {
        connectionList.get(id).task.init()
    }

    @Override
    void prepareToFlightAll() {
        connectionList.each {
            it.task.init()
        }
    }

    @Override
    void changeOrientation(Quaternion orient, int id) {
        def msg = new MavLink.MSG_SET_ATTITUDE_TARGET(
                MavLink.MAV_SYSTEM_ID, MavLink.MAV_COMPONENT_ID, 0L,
                orient.q,
                0f,0f,0f,
                0.6f,
                1, 1,
                (int)0b001_11_111
        )

        msg = MavLink.Message.decodeMessage(msg.revert().encode())
        connectionList.get(id).task.addTask(msg,20L)
    }
}

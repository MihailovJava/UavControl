package mavlink

import mavlink.net.MavConnection
import org.springframework.beans.factory.annotation.Autowired

class MavAircraft extends Observable implements Observer {
    MavConnection connection
    MavTask task
    MavLinkPosition currentPosition
    MavLinkOrientation currentOrientation
    List<MavLinkPosition> positions = new ArrayList<>()
    List<MavLinkOrientation> orientations = new ArrayList<>()
    int id

    FormationControl formationControl

    @Override
    void update(Observable o, Object arg) {
        def message = arg as MavLink.Message
        switch (message.getMessageId()) {
            case MavLink.MSG_ID_LOCAL_POSITION_NED:
                message = message as MavLink.MSG_LOCAL_POSITION_NED
                currentPosition = new MavLinkPosition(
                        nedX: message.x, nedY: message.y, nedZ: message.z,
                        nedVX: message.vx, nedVY: message.vy, nedVZ: message.vz,
                        time: System.currentTimeMillis())
               // println currentPosition
                positions.add(currentPosition)
                formationControl.checkMyPosition(currentPosition,id)
                setChanged()
                notifyObservers([currentPosition,id])
                break
            case MavLink.MSG_ID_ATTITUDE:
                message = message as MavLink.MSG_ATTITUDE
                currentOrientation = new MavLinkOrientation(psi: message.yaw,theta: message.pitch,gama: message.roll,
                        time: System.currentTimeMillis())
                orientations.add(currentOrientation)
                formationControl?.checkMyOrientation(currentOrientation,id)
                setChanged()
                notifyObservers([currentOrientation,id])
                break
        }
    }
}

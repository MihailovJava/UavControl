package mavlink

import mavlink.net.Client
import mavlink.net.UnitySend

import java.nio.ByteBuffer
import java.nio.ByteOrder


class MavLinkUnity implements Observer {

    UnitySend sender

    void sendInitializeCommand(List<MavAircraft> aircraft){
        def buffer = ByteBuffer.allocate(3*8 + aircraft.size()*(1+3+3)*8)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.putDouble(1)
        buffer.putDouble(aircraft.size()) // Aircraft Count
        buffer.putDouble(0) // init
        aircraft.each {
            MavLinkPosition position = it.currentPosition ?: new MavLinkPosition(nedX: 0,nedY: 0,nedZ: 0)
            MavLinkOrientation orientation = it.currentOrientation ?: new MavLinkOrientation(psi: 0,theta: 0,gama: 0)
            int id = it.id
            buffer.putDouble(id)
            buffer.putDouble(position.nedX)
            buffer.putDouble(position.nedY)
            buffer.putDouble(-position.nedZ)
            buffer.putDouble(orientation.psi)
            buffer.putDouble(orientation.theta)
            buffer.putDouble(orientation.gama)
        }
       sender.byteMsgs.add(buffer)

    }

    @Override
    void update(Observable o, Object arg) {
        if (arg[0] instanceof MavAircraft) {
            MavAircraft aircraft = arg[0] as MavAircraft
            MavLinkPosition position = aircraft.currentPosition ?: new MavLinkPosition(nedX: 0,nedY: 0,nedZ: 0)
            MavLinkOrientation orientation = aircraft.currentOrientation ?: new MavLinkOrientation(psi: 0,theta: 0,gama: 0)
            int id = arg[1] as Integer
         //   println "upd " + position + " " + orientation + " "+ id
            def buffer = ByteBuffer.allocate(3 * 8 + (1 + 3 + 3) * 8)
            buffer.order(ByteOrder.LITTLE_ENDIAN)
            buffer.putDouble(1)
            buffer.putDouble(1) // Aircraft Count
            buffer.putDouble(1) // MOVE
            buffer.putDouble(id)
            buffer.putDouble(position.nedX)
            buffer.putDouble(position.nedY)
            buffer.putDouble(-position.nedZ)
            buffer.putDouble(orientation.gama)
            buffer.putDouble(orientation.theta)
            buffer.putDouble(-orientation.psi)

            sender.byteMsgs.add(buffer)
        }
    }
}

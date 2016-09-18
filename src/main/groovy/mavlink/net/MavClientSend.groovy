package mavlink.net


import mavlink.MavLink
import mavlink.util.Pair

import javax.annotation.PostConstruct
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue


class MavClientSend implements Runnable {


    Client client

    Queue<Pair<MavLink.Message, Long>> msgs = new ConcurrentLinkedQueue<>()
    Queue<ByteBuffer> byteMsgs = new ConcurrentLinkedQueue<>()
    private final int SECOND_DELAY = 1000

    @PostConstruct
    void afterInit(){
        new Thread(this,"MAV_SEND " + System.currentTimeMillis()).start()
    }
    @Override
    void run() {
        byte count = 0
        while (true) {
            long delay = SECOND_DELAY;
            if (client?.isConnect()) {
                MavLink.Message msg = new MavLink.MSG_HEARTBEAT(
                        MavLink.MAV_SYSTEM_ID,
                        MavLink.MAV_COMPONENT_ID,
                        0L,
                        MavLink.MAV_TYPE_QUADROTOR,
                        MavLink.MAV_AUTOPILOT_PIXHAWK,
                        MavLink.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED,
                        MavLink.MAV_STATE_UNINIT,
                        3)

                try {
                    if (!byteMsgs.empty) {
                        msg = MavLink.Message.decodeMessage(byteMsgs.poll().array())
                    } else if (!msgs.empty) {
                        def pair = msgs.poll()
                        msg = pair.key
                        delay = pair.value
                    }
                    msg.sequenceIndex = count
               //     println(msg.toString() + " time " + System.currentTimeMillis())
                    client.send(msg.encode())
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
            Thread.sleep(delay)
            count++


        }
    }
}

package mavlink.net

import genetic.genetic.system.Pair
import mavlink.MavLink
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue


class MavClientSend implements Runnable {


    Client client

    Queue<Pair<MavLink.Message, Long>> msgs = new ConcurrentLinkedQueue<>()
    Queue<ByteBuffer> byteMsgs = new ConcurrentLinkedQueue<>()
    private final int SECOND_DELAY = 1000

    MavClientSend() {
        new Thread(this).start()
    }

    @Override
    void run() {
        byte count = 0
        while (true) {
            long delay = SECOND_DELAY;
            try {
                synchronized (client) {
                    if (client?.connect()) {
                        MavLink.Message msg = new MavLink.MSG_HEARTBEAT(
                                MavLink.MAV_SYSTEM_ID,
                                MavLink.MAV_COMPONENT_ID,
                                0L,
                                MavLink.MAV_TYPE_QUADROTOR,
                                MavLink.MAV_AUTOPILOT_PIXHAWK,
                                MavLink.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED,
                                MavLink.MAV_STATE_UNINIT,
                                3)
                        synchronized (msgs) {
                            try {
                                if (!byteMsgs.empty) {
                                    msg = MavLink.Message.decodeMessage(byteMsgs.poll().array())
                                } else {
                                    def pair = msgs.poll()
                                    msg = pair.key
                                    delay = pair.value
                                }
                                msg.sequenceIndex = count
                                client.send(msg.encode())
                            } catch (Exception e) {
                                e.printStackTrace()
                            }
                        }
                        sleep(delay)
                        count++
                    }
                }

            } catch (Exception e) {
            }
        }
    }
}

package mavlink.net

import mavlink.MavLink

import javax.annotation.PostConstruct
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue


class UnitySend implements Runnable {
    Client client
    Queue<ByteBuffer> byteMsgs = new ConcurrentLinkedQueue<>()

    @PostConstruct
    void afterInit() {
        new Thread(this,"UNITY_SEND " + System.currentTimeMillis()).start()
    }

    @Override
    void run() {
        while (true) {
            if (client?.isConnect()) {

                if (!byteMsgs.empty) {
                    client.send(byteMsgs.poll().array())
                }
            }

            Thread.sleep(1)
        }
    }
}

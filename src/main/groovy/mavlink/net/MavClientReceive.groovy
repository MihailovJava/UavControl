package mavlink.net

import genetic.genetic.system.Pair
import mavlink.MavLink
import mavlink.utils.MavLinkCRC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue


class MavClientReceive implements Runnable {

    Client client
    Queue<Pair<MavLink.Message, Long>> msgs = new ConcurrentLinkedQueue<>()

    MavClientReceive() {
        new Thread(this).start()
    }

    @Override
    void run() {

        while (true) {
            try {
                synchronized (client) {
                    if (client?.connect()) {
                        byte[] received = client.receive()
                        try {
                            println convertToHex(received.encodeHex().toString())

                            def message = MavLink.Message.decodeMessage(received).revert()
                            println message
                            int crc = MavLinkCRC.calcCRC(Arrays.copyOf(received, received.length - 2))
                            println((crc & 0xFF) + ' ' + (received[received.length - 2] & 0xFF))
                            println(((crc >> 8) & 0xFF) + ' ' + (received[received.length - 1] & 0xFF))
                            println((crc & 0xFF) == (received[received.length - 2] & 0xFF))
                            println(((crc >> 8) & 0xFF) == (received[received.length - 1] & 0xFF))
                            msgs.add(new Pair<MavLink.Message, Long>(message,System.currentTimeMillis()))

                        } catch (Exception e) {
                            e.printStackTrace()
                        }
                    }
                }

            } catch (Exception e) {
            }
        }


    }


    String convertToHex(String writable) {
        def builder = new StringBuilder(writable)
        int size = writable.length() / 2;
        for (int i = 0; i < size; i++) {
            builder.insert(i * 5, ' 0x')
        }
        return builder.toString()
    }
}

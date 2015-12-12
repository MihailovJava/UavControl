package mavlink.net

import mavlink.MavLink

import javax.annotation.PostConstruct


class MavClientReceive extends Observable implements Runnable {

    Client client


    @PostConstruct
    void afterInit(){

        new Thread(this,"MAV_RECEIVE " + System.currentTimeMillis()).start()
    }

    @Override
    void run() {

        while (true) {

            if (client?.isConnect()) {
                byte[] received = client.receive()
                if(received != null) {
                    try {
                       // println convertToHex(received.encodeHex().toString())

                        def message = MavLink.Message.decodeMessage(received).revert()
           //             println message
        //                int crc = MavLinkCRC.calcCRC(Arrays.copyOf(received, received.length - 2))
                        //   println((crc & 0xFF) + ' ' + (received[received.length - 2] & 0xFF))
                        //  println(((crc >> 8) & 0xFF) + ' ' + (received[received.length - 1] & 0xFF))
                        //    println((crc & 0xFF) == (received[received.length - 2] & 0xFF))
                        //    println(((crc >> 8) & 0xFF) == (received[received.length - 1] & 0xFF))
                        setChanged()
                        notifyObservers(message)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }

            }
            Thread.sleep(1)

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

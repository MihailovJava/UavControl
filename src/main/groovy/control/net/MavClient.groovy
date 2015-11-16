package control.net

import com.google.common.primitives.Bytes
import control.mavlink.MavLink
import control.mavlink.MavLinkCRC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MavClient implements Runnable {

    @Autowired
    Client client

    byte[] mavPacket = [];
    byte[] packet = new byte[6 + 2];

    MavClient() {
        new Thread(this).start()
    }

    @Override
    void run() {
        def writer = new PrintWriter("dump.txt")
        byte count = 0
        byte[] prefix;
        boolean flag = true;
        try {
            while (true) {


                if (client?.connect()) {

                    //client.send(mavPacket);


                    byte[] received = client.receive()
                    def message


                    if(received.length == 1){
                        prefix = received
                    }else
                    if(received.length > 6){
                        try {
                            def bytes = Bytes.concat(prefix, received)

                            String toHex = convertToHex(bytes.encodeHex().toString())
                            println toHex
                            println MavLink.Message.decodeMessage(bytes)
                            int crc = MavLinkCRC.calcCRC(Arrays.copyOf(bytes,bytes.length-2));

                            println (crc & 0xFF)
                            println (crc >> 8)

                            writer.append("packet $count \n")
                            writer.append(toHex)
                            writer.append('\n')
                            println count++
                        } catch (Exception e){
                            e.printStackTrace()
                        }

                    }

                    /* def bytes = Bytes.concat(fe, received)
                if(received.length > 1) {
                    try {
                        println MavLink.Message.decodeMessage(bytes)
                    }catch (Exception e){

                    }
                }
                if(received.length > 1) {
                    int crc = MavLinkCRC.calcCRC(Arrays.copyOf(bytes, bytes.length - 2));
                    println bytes[bytes.length - 2] == (crc & 0xFF)
                    println bytes[bytes.length - 1] == (crc >> 8)
                    //  client.send((byte[])[0xFE,0x09,0x00,0xFF,0xBE,0x00,0x00,0x00,0x00,0x00,0x06,0x08,0x00,0x00,0x03,0x28,0x42]);
                    def packet = [0xFE, 0x06, count, 0xFF, 0xBE, 0x42, 0x02, 0x00, 0x01, 0x01, 0x02, 0x01];
                    crc = MavLinkCRC.calcCRC((byte[]) packet);
                    packet << (crc & 0xFF)
                    packet << (crc >> 8)
                }

                client.send((byte[])packet)*/

                    // sleep(1)
                    if (flag){
                        MavLink.Message msg = new MavLink.MSG_REQUEST_DATA_STREAM(2,2,1,1,1)
                        client.send(msg.encode())
                        flag = false;
                    }
                }
            }
        }catch (Exception e){}
        finally {
            writer.flush()
            writer.close()
        }
    }

    String convertToHex(String writable) {
        def builder = new StringBuilder(writable)
        int size = writable.length() / 2;
        for (int i = 0; i < size  ; i++) {
            builder.insert(i*5,' 0x')
        }
        return builder.toString()
    }
}

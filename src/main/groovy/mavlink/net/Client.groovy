package mavlink.net

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.nio.charset.Charset


class Client implements TcpClient {

    Socket socket

    String host
    Integer port

    boolean connect = false
    private OutputStream outputStream
    private InputStream inputStream


    public Client(String host, int port) {
        try {
            socket = new Socket(host, port)
            this.port = port
            this.host = host
            connect = true
            outputStream = socket.outputStream
            inputStream = socket.inputStream
        } catch (Exception e) {


        }
        println("New connection " + host + ":" + port)
        /* new Thread({
             while (socket == null) {
                 try {

                 } catch (Exception e) {

                     e.println("Server offline " + host + ":" + port)
                     e.printStackTrace()
                     Thread.sleep(1000)
                 }
             }
         }).start()*/
    }

    @Override
    boolean connect() {
        return isConnect()
    }

    @Override
    boolean send(byte[] data) {
        if (outputStream != null && !socket?.isClosed()) {

            outputStream.write(data)
      //      println "Sent " + data.toString() + " " + host + ":" + port
            return true;
        }
        return false
    }

    @Override
    byte[] receive() {
        if (inputStream != null && !socket?.isClosed()) {
            byte[] tmp = new byte[2 * 1024]
            byte b
            int size = 0
            while ((b = inputStream.read()) != 0xFE);
            tmp[size++] = 0xFE
            tmp[size++] = inputStream.read()
            for (int i = 0; i < tmp[1] + 4 + 2; i++) {
                tmp[size++] = inputStream.read()
            }
      //      print host + ":" + port + " "
            return Arrays.copyOf(tmp, size)
        }
        return null
    }


}

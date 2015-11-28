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
    Thread connectionThread

    public boolean connect() {
        if (connectionThread == null) {
            connectionThread = new Thread({
                while (socket == null) {
                    try {
                        socket = new Socket(host, port)
                        connect = true
                    } catch (Exception e) {
                        e.println("Server offline")
                        Thread.sleep(1000)
                    }
                }
            })
        }

        return isConnect()
    }

    @Override
    boolean send(byte[] data) {
        if (!socket.isClosed()) {
            socket.outputStream.write(data)
            println "Sent " + data
            return true;
        }
        return false
    }

    @Override
    byte[] receive() {
        if (!socket.isClosed()) {
            byte[] tmp = new byte[2 * 1024]
            byte b
            int size = 0
            while ((b = socket.inputStream.read()) != 0xFE);
            tmp[size++] = 0xFE
            tmp[size++] = socket.inputStream.read()
            for (int i = 0; i < tmp[1] + 4 + 2; i++) {
                tmp[size++] = socket.inputStream.read()
            }

            return Arrays.copyOf(tmp, size)
        }
        return null
    }


}

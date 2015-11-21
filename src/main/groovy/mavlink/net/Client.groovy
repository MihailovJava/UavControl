package mavlink.net

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.nio.charset.Charset

@Component
class Client implements TcpClient{


    @Autowired
    Environment environment


    Socket socket

    String host
    Integer port

    @PostConstruct
    void init(){
        host = environment.getProperty("tcp.client.host")
        port = environment.getProperty("tcp.client.port") as Integer
    }

    public boolean connect() {
        while (socket == null) {
            try {
                socket = new Socket(host, port)
            } catch (Exception e) {
                e.println("Server offline")
                Thread.sleep(1000)
            }
        }
        return socket.isConnected()
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
            byte[] tmp = new byte[2*1024]
            int size = socket.inputStream.read(tmp)
            return Arrays.copyOf(tmp,size)
        }
        return null
    }

    public String send(String data) {
        byte[] byteData = Charset.forName("UTF-8").encode(data).array()
        send(byteData)
    }


}

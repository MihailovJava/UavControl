package spring

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import mavlink.MavLinkController
import mavlink.net.Client
import mavlink.net.MavClientReceive
import mavlink.net.MavClientSend
import mavlink.net.MavConnection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment
import tcp.server.org.springframework.tcp.EnableTcpControllers

import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.IllegalClassFormatException
import java.lang.instrument.Instrumentation
import java.nio.ByteBuffer
import java.nio.HeapByteBuffer
import java.security.ProtectionDomain

@Configuration
@EnableTcpControllers
@PropertySource("classpath:mavlink/application.properties")
@ComponentScan("mavlink")
class ControllerConfig {


    public static void main(String[] args) {
        def context = new AnnotationConfigApplicationContext(ControllerConfig.class)
    }
  

    @Bean
    @Autowired
    List<MavConnection> mavConnectionList(Environment environment,Client client){
        List<MavConnection> connectionList = new ArrayList<>()
        def count = environment.getProperty("tcp.client.count") as Integer
        for (int i = 0 ; i < count ; i++) {
            connectionList.add(new MavConnection(
                    receive: new MavClientReceive(client:  client),
                    send: new MavClientSend(client: client)
            ))
        }
        return connectionList
    }

    int instance = 0

    @Bean
    @Scope("prototype")
    @Autowired
    Client client(Environment environment){
        def host = environment.getProperty("tcp.client.host")
        def port = environment.getProperty("tcp.client.port") as Integer
        port += instance * 10
        instance++
        def client = new Client(port: port, host: host)
        client.connect()
        return client
    }

}


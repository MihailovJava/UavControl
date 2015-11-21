package mavlink

import mavlink.net.MavClient
import mavlink.net.TcpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.reader.StreamReader

import javax.annotation.PostConstruct
import java.nio.ByteBuffer

@Component
class MavTask {
    @Autowired
    MavClient mavClient

    @Autowired
    Environment environment


    @PostConstruct
    void init() {
        def file = new File(environment.getProperty('mavlink.task.file.name'))/*
        file.withReader {r ->
            def lines = r.readLines()
            lines.each { line ->
                def bytes = line.getBytes()
                mavClient.byteMsgs.add(ByteBuffer.wrap(bytes))
            }
        }
        */
        mavClient.msgs.add(new MavLink.MSG_REQUEST_DATA_STREAM(
                MavLink.MAV_SYSTEM_ID,
                MavLink.MAV_COMPONENT_ID,
                20,
                MavLink.MAV_SYSTEM_ID,
                MavLink.MAV_COMPONENT_ID,
                0,
                1

        ))
        mavClient.msgs.add(new MavLink.MSG_REQUEST_DATA_STREAM(
                MavLink.MAV_SYSTEM_ID,
                MavLink.MAV_COMPONENT_ID,
                20,
                MavLink.MAV_SYSTEM_ID,
                MavLink.MAV_COMPONENT_ID,
                0,
                1
        ))
    }

}

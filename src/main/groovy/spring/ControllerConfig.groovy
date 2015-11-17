package spring

import mavlink.net.MavClient
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import tcp.server.org.springframework.tcp.EnableTcpControllers

@Configuration
@EnableTcpControllers
@PropertySource("classpath:mavlink/application.properties")
@ComponentScan("mavlink")
class ControllerConfig {

    public static void main(String[] args) {
        def context = new AnnotationConfigApplicationContext(ControllerConfig.class)
        def bean = context.getBean(MavClient.class)
       /* bean.msgs.add(new MavLink.MSG_REQUEST_DATA_STREAM(
                (short) 2,
                (short) 2,
                2,

        )
        )*/
    }
}

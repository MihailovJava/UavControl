package control.spring

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import tcp.server.org.springframework.tcp.EnableTcpControllers

@Configuration
@EnableTcpControllers
@PropertySource("classpath:tcp/application.properties")
@ComponentScan("control")
class ControllerConfig {

    public static void main(String[] args) {
        def context = new AnnotationConfigApplicationContext(ControllerConfig.class)

    }
}

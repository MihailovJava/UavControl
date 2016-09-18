package mavlink.spring

import com.google.gson.Gson
import hungarian.spring.HungarianConfig
import mavlink.FormationControl
import mavlink.MavAircraft
import mavlink.MavLinkController
import mavlink.MavLinkUnity
import mavlink.MavTask
import mavlink.PixHawkMavLinkService
import mavlink.control.commands.Command
import mavlink.net.Client
import mavlink.net.MavClientReceive
import mavlink.net.MavClientSend
import mavlink.net.MavConnection
import mavlink.net.UnitySend
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment

import javax.swing.JFrame
import javax.swing.WindowConstants
import java.util.concurrent.ConcurrentLinkedDeque

@Configuration
@PropertySource("classpath:mavlink/application.properties")
@ComponentScan("mavlink")
//@SpringBootApplication
class ControllerConfig {

    private static Log logger = LogFactory.getLog(ControllerConfig.class);

    public static void main(String[] args) {
        //   SpringApplication.run(ControllerConfig.class)
        def context = new AnnotationConfigApplicationContext(ControllerConfig.class, HungarianConfig.class)
        def bean = context.getBean(JFrame.class)
        bean.setVisible(true)
        //def bean = context.getBean(PixHawkMavLinkService.class)
        //   bean.changeOrientation(new Quaternion((float)(3*Math.PI/4),0,0),0)
        //  MavLinkService service = context.getBean(MavLinkService.class)
        // service.prepareToFlight(0)
        //  service.flightHere(new MavLinkPosition(nedX: 11.2*5,nedY: 11.2*5,nedZ: -24),0)
        //Thread.sleep(40000)
        // logger.info( service.getCurrentPosition(0))
    }

    @Autowired
    Environment environment

    @Bean
    MavLinkController mavLinkController() {
        def controller = new MavLinkController(connectionList: aircraftList())
        controller.connectionList.each { it.addObserver(controller) }
        return controller
    }

    @Bean
    @Scope("prototype")
    FormationControl formationControl() {
        def id = environment.getProperty("formation.init.id") as Integer
        def deltaPos = environment.getProperty("formation.delta.pos") as Float
        def deltaYaw = environment.getProperty("formation.delta.yaw") as Float
        def formationControl = new FormationControl(
                formationId: id,
                commandDeque: new ConcurrentLinkedDeque<Command>(),
                deltaPos: deltaPos,
                deltaYaw: deltaYaw)
        return formationControl
    }

    @Bean
    List<MavAircraft> aircraftList() {
        List<MavAircraft> aircraftArrayList = new ArrayList<>()
        def count = environment.getProperty("tcp.client.count") as Integer
        for (int i = 0; i < count; i++) {
            def aircraft = aircraft()
            aircraft.id = i
            aircraft.formationControl = formationControl()
            aircraft.formationControl.positionInList = i
            aircraft.formationControl.positionInFormation = i
            aircraftArrayList.add(aircraft)
        }
        return aircraftArrayList
    }

    @Bean
    Gson gson() {
        new Gson()
    }


    @Bean
    @Scope("prototype")
    MavAircraft aircraft() {
        def connection = connection()
        def task = task()
        task.mavClientSend = connection.send
        def aircraft = new MavAircraft(connection: connection, task: task)
        aircraft.connection.receive.addObserver(aircraft)
        return aircraft
    }

    @Bean
    PixHawkMavLinkService pixHawkMavLinkService() {
        def service = new PixHawkMavLinkService()
        service.connectionList = aircraftList()
        def unity = mavLinkUnity()
        unity.sendInitializeCommand(service.connectionList)
        service.connectionList.each {
            def control = it.formationControl
            control.service = service
            control.afterInit()
            it.addObserver(unity)
        }




        return service
    }

    @Bean
    @Scope("prototype")
    MavTask task() {
        return new MavTask()
    }

    @Bean
    MavLinkUnity mavLinkUnity() {
        new MavLinkUnity(sender: sender())
    }

    @Bean
    UnitySend sender() {
        new UnitySend(client: netClient())
    }

    @Bean
    Client netClient() {
        def host = environment.getProperty("net.host")
        def port = environment.getProperty("net.port") as Integer
        def client = new Client(host, port)
        return client
    }

    @Bean
    JFrame form() {
        JFrame mainForm = new JFrame(environment.getProperty("form.title"))
        mainForm.setSize(
                environment.getProperty("form.width") as Integer,
                environment.getProperty("form.height") as Integer);
        mainForm.setLocationRelativeTo(null);
        mainForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return mainForm
    }

    @Bean
    @Scope("prototype")
    MavConnection connection() {
        Client client = client()
        def receive = mavClientReceive()
        receive.client = client
        def send = mavClientSend()
        send.client = client
        return new MavConnection(receive: receive, send: send)
    }

    @Bean
    @Scope("prototype")
    MavClientReceive mavClientReceive() {
        return new MavClientReceive()
    }

    @Bean
    @Scope("prototype")
    MavClientSend mavClientSend() {
        return new MavClientSend()
    }

    int instance = 0

    @Bean
    @Scope("prototype")
    Client client() {
        def host = environment.getProperty("tcp.client.host")
        def port = environment.getProperty("tcp.client.port") as Integer
        port += instance * 10
        instance++
        def client = new Client(host, port)
        return client
    }

}


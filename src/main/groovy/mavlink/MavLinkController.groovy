package mavlink

import genetic.repository.FormationRepository
import genetic.repository.InitPositionRepository
import mavlink.net.MavConnection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


class MavLinkController implements Observer {

    List<FormationControl> connectionList

    @Autowired
    Environment environment


    File[] files

    @PostConstruct
    void afterInit() {
        files = [
                new File(environment.getProperty('mavlink.control.file.name.position')),
                new File(environment.getProperty('mavlink.control.file.name.orientation'))]
        files.each {it.withWriter { w -> w.write("") }}
    }

    @Override
    void update(Observable o, Object arg) {
        List<Object> args = arg as ArrayList
        if( args.get(0) instanceof MavLinkPosition ) {
            files[0] << (arg.toString() + '\n')
        }
        if( args.get(0) instanceof MavLinkOrientation ) {
            files[1] << (arg.toString() + '\n')
        }
    }

    @PreDestroy
    void beforeDestroy() {
        files.each {it.deleteOnExit()}
    }
}

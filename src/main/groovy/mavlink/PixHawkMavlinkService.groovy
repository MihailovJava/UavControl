package mavlink

import org.springframework.stereotype.Component

@Component
class PixHawkMavLinkService implements MavLinkService {




    @Override
    void flightHere(MavLinkPosition target) {

    }

    @Override
    MavLinkPosition getCurrentPosition() {
        return null
    }

    @Override
    MavLinkPosition getTargetPosition() {
        return null
    }

    @Override
    boolean prepareToFlight() {
        return false
    }
}

package mavlink


interface MavLinkService {
    void flightHere(MavLinkPosition target)
    MavLinkPosition getCurrentPosition()
    MavLinkPosition getTargetPosition()
    boolean prepareToFlight()
}
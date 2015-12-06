package mavlink


interface MavLinkService {
    void flightHere(MavLinkPosition target,int id)
    MavLinkPosition getCurrentPosition(int id)
    int getCount()
    void prepareToFlight(int id)
    void prepareToFlightAll()
}
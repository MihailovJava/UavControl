package mavlink

import math.Quaternion


interface MavLinkService {
    void flightHere(MavLinkPosition target,int id)
    MavLinkPosition getCurrentPosition(int id)
    int getCount()
    void prepareToFlight(int id)
    void prepareToFlightAll()
    void changeOrientation(Quaternion orient,int id)
}
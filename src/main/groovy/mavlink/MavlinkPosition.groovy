package mavlink


class MavLinkPosition {
    float nedX, nedY, nedZ
    float nedVX, nedVY, nedVZ
    long time

    @Override
    String toString() {
        return "[ x = $nedX y = $nedY z = $nedZ  vx = $nedVX vy = $nedVY vz = $nedVZ time = $time]";
    }


    MavLinkPosition plus(MavLinkPosition b){
        new MavLinkPosition(nedX: nedX + b.nedX, nedY: nedY + b.nedY, nedZ: nedZ + b.nedZ)
    }


    MavLinkPosition minus(MavLinkPosition b){
        new MavLinkPosition(nedX: nedX - b.nedX, nedY: nedY - b.nedY, nedZ: nedZ - b.nedZ)
    }

    float length(){
        return (nedX**2 + nedY**2 + nedZ**2)**(0.5)
    }
}

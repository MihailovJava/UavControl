package mavlink


class MavLinkOrientation {
    float psi,theta,gama
    long time

    @Override
    String toString() {
        return "[ psi = $psi theta = $theta gama = $gama time = $time]"
    }
}

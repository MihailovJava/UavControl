package mavlink


interface MavLinkObserver {

    void receivedMessage(MavLink.Message message)
}
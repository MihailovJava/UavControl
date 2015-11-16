package control.net


interface TcpClient {
    boolean connect()
    boolean send(byte[] data)
    byte[] receive()
}
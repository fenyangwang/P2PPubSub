import java.io.Serializable;

public class PeerAddress implements Serializable {
    private int id;
    private String ip;
    private int port;

    public PeerAddress(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}

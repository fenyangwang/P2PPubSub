import java.io.Serializable;
import java.util.ArrayList;

public class PeerInfo implements Serializable {
    int id;
    String ip;
    int port;
    ArrayList<String> subscriptionList;
    PeerInfo(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        subscriptionList = new ArrayList<>();
    }
    PeerInfo(int id, String ip, int port, ArrayList<String> subscriptionList) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.subscriptionList = subscriptionList;
    }
}

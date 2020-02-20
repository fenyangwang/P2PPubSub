import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeerInfo implements Serializable {
    int id;
    String ip;
    int port;
    List<String> subscription;
    PeerInfo(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        subscription = new ArrayList<>();
    }
}

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PeerInfo implements Serializable {
    int id;
    String ip;
    int port;
    Set<Category> subscriptionList;

    PeerInfo(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        subscriptionList = new HashSet<Category>();
    }
    
    PeerInfo(int id, String ip, int port, Set<Category> subscriptionList) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.subscriptionList = subscriptionList;
    }

    @Override
    public int hashCode() {
        return (this.ip + this.port).hashCode();
    }

	@Override
    public boolean equals(Object other) {
        return ((PeerInfo)other).port == this.port && 
                ((PeerInfo)other).ip.equals(this.ip);
    }
}

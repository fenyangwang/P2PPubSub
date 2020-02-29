import java.io.Serializable;
import java.util.Set;

public class Request implements Serializable {
    public PeerInfo peerInfo;
    public String command;
    public Message message;
    // public Set<Category> subscriptionList;  // for wfy

    public Request(PeerInfo peerInfo, String command) {
        this.peerInfo = peerInfo;
        this.command = command;
        this.message = null;
    }

    public Request(Message message, String command) {
        this.message = message;
        this.command = command;
        this.peerInfo = null;
    }

}

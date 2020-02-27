import java.io.Serializable;

public class Request implements Serializable {
    public PeerInfo peerInfo;
    public String command;
    public Message message;
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

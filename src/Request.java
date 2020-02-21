import java.io.Serializable;

public class Request implements Serializable {
    public PeerInfo peerInfo;
    public String message;
    public Request(PeerInfo peerInfo, String message) {
        this.peerInfo = peerInfo;
        this.message = message;
    }
}

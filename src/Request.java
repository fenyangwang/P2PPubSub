import java.io.Serializable;
import java.util.List;


public class Request implements Serializable {
    public PeerInfo peerInfo;
    public String command;
    public Message message;
    public List<Category> categories;

    public Request(PeerInfo peerInfo, String command) {
        this.peerInfo = peerInfo;
        this.command = command;
        this.message = null;
        this.categories = null;
    }

    public Request(Message message, String command) {
        this.message = message;
        this.command = command;
        this.peerInfo = null;
        this.categories = null;
    }

    public Request(List<Category> categories, Message message, String command) {
        this.categories = categories;
        this.message = message;
        this.command = command;
        this.peerInfo = null;
    }

}

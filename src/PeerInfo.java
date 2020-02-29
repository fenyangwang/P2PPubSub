import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeerInfo implements Serializable {
    int id;
    String ip;
    int port;
    Set<Category> subscriptionList;
    List<PeerAddress> fingerTable;
    PeerAddress successorAddress;
    PeerAddress predecessorAddress;

    public PeerInfo(int id, String ip, int port, PeerAddress predecessorAddress, PeerAddress successorAddress, Set<Category> subscriptionList, List<PeerAddress> fingerTable) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.predecessorAddress = predecessorAddress;
        this.successorAddress = successorAddress;
        this.subscriptionList = subscriptionList;
        this.fingerTable = fingerTable;
    }

    void showDetails() {
        showPredecessorDetail();
        showSuccessorDetail();
    }

    void showPredecessorDetail() {
        if (predecessorAddress == null) {
            System.out.println("    predecessor is null, no detail to show");
            return;
        }
        System.out.println("    predecessor id: " + predecessorAddress.getId());
        System.out.println("    predecessor id: " + predecessorAddress.getIp());
        System.out.println("    predecessor id: " + predecessorAddress.getPort());
    }

    void showSuccessorDetail() {
        if (successorAddress == null) {
            System.out.println("    successor is null, no detail to show");
            return;
        }
        System.out.println("    successor id: " + successorAddress.getId());
        System.out.println("    successor id: " + successorAddress.getIp());
        System.out.println("    successor id: " + successorAddress.getPort());
    }

    PeerAddress getAddress() {
        return new PeerAddress(id, ip, port);
    }

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

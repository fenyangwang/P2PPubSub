import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeerInfo implements Serializable {
    int id;
    String ip;
    int port;
    Set<Category> validCategorySet;
    Set<Category> subscriptionList;
    List<PeerAddress> fingerTable;
    List<PeerAddress> successorList;
    PeerAddress predecessorAddress;

    public PeerInfo(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        validCategorySet = new HashSet<Category>();
        subscriptionList = new HashSet<Category>();
    }

    public PeerInfo(int id, String ip, int port, Set<Category> subscriptionList) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        validCategorySet = new HashSet<Category>();
        this.subscriptionList = subscriptionList;
    }

    public PeerInfo(Set<Category> validCategorySet, int id, String ip, int port) {
        this.validCategorySet = validCategorySet;
        this.id = id;
        this.ip = ip;
        this.port = port;
        subscriptionList = new HashSet<Category>();
    }


    public PeerInfo(int id, String ip, int port, PeerAddress predecessorAddress, List<PeerAddress> successorList, 
                    Set<Category> subscriptionList, List<PeerAddress> fingerTable) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.predecessorAddress = predecessorAddress;
        this.successorList = successorList;
        this.subscriptionList = subscriptionList;
        this.fingerTable = fingerTable;
    }

    public List<PeerAddress> getSuccessorList() {
        return successorList;
    }

    public void showDetails() {
        showPredecessorDetail();
        showSuccessorDetail();
    }

    private void showPredecessorDetail() {
        if (predecessorAddress == null) {
            System.out.println("    predecessor is null, no detail to show");
            return;
        }
        System.out.printf("    predecessor id: %d, ip: %s, port: %d\n", predecessorAddress.getId(), predecessorAddress.getIp(), predecessorAddress.getPort());
    }

    private void showSuccessorDetail() {
        if (successorList == null) {
            System.out.println("    successorList is null, no detail to show");
            return;
        }
        for (int i = 0; i < successorList.size(); i++) {
            PeerAddress successorAddress = successorList.get(i);
            if (successorAddress == null) {
                continue;
            }
            System.out.printf("    entry: %d, successor id: %d, ip: %s, port: %d\n", i, successorAddress.getId(), successorAddress.getIp(), successorAddress.getPort());
        }

    }

    public PeerAddress getAddress() {
        return new PeerAddress(id, ip, port);
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

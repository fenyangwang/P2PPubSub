import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Peer {
    private static double gamma;
    int id;
    String ip;
    int port;

    private PeerInfo bootPeer = new PeerInfo(getHash("127.0.0.1:" + 8001),"127.0.0.1", 8001);
    private List<PeerInfo> fingerTable;
    private List<String> subscriptionList;
    public PeerInfo predecessor;
    private PeerInfo successor;
    private Listener listener;
    private FingersFixer fingersFixer;
    private PredecessorFixer predecessorFixer;
    private Stabilizer stabilizer;

    public Peer(String ip, int port) {
        this.id = getHash(ip + ":" + port);
        this.ip = ip;
        this.port = port;

        fingerTable = new ArrayList<>();

        subscriptionList = new ArrayList<>();

        this.listener = new Listener(this);
        this.fingersFixer = new FingersFixer(this);
        this.predecessorFixer = new PredecessorFixer(this);
        this.stabilizer = new Stabilizer(this);

        new Thread(listener).start();

        create();
    }

    void sendMessage(int port, String message) {
        RPC.sendMessage(port, message);
    }


    void create() {
        predecessor = null;
        System.out.println("new node with id: " + id + " ip: " + ip + " port: " + port + " is created");
        successor = new PeerInfo(id, ip, port);
        //successor = null;
        for (int i = 0; i <= 3; i++) {
            fingerTable.add(null);
        }
    }

    void join() {
        if (id == bootPeer.id) {
            System.out.println("The first peer has already joined.");
            new Thread(fingersFixer).start();
            new Thread(predecessorFixer).start();
            new Thread(stabilizer).start();
            return;
        }
        predecessor = null;
        String message = id + " " + bootPeer.ip + " " + bootPeer.port + " findSucc";
        successor = RPC.findSuccessor(message);
        updateFingerTable(0, successor);
        //notifySuccessor();
        System.out.println("Peer with id: " + id + " ip: " + ip + " port: " + port + " has successor: ");
        System.out.println("succ id: " + successor.id);
        System.out.println("succ id: " + successor.ip);
        System.out.println("succ id: " + successor.port);
        System.out.println("succ subscription list size: " + successor.subscription.size());
        System.out.println();
        new Thread(fingersFixer).start();
        new Thread(predecessorFixer).start();
        new Thread(stabilizer).start();
    }

    PeerInfo getSuccessor() {
       return successor;
    }

    PeerInfo getPredecessor() {
        return predecessor;
    }

    PeerInfo findSuccessor(int targetId) {
        if (id == successor.id) {
            return successor;
        }

        if (id > successor.id) {
            if (targetId > id || targetId <= successor.id) {
                return successor;
            }
        } else if (id < successor.id && id < targetId && targetId <= successor.id) {
            return successor;
        }
        PeerInfo closestPrecedingPeer = getClosetPrecedingPeer(targetId);
        String message = targetId + " " + closestPrecedingPeer.ip + " " + closestPrecedingPeer.port + " findSucc";
        return RPC.findSuccessor(message);
    }

    PeerInfo getClosetPrecedingPeer(int peerId) {
        int max = -1;
        int entryIndex = -1;
        for (int i = 3; i >= 0; i--) {
            PeerInfo peerInfo = fingerTable.get(i);
            if (peerInfo == null) {
                continue;
            }
            int entryId = fingerTable.get(i).id;
            if (entryId < peerId) {
                if (entryId > max) {
                    max = entryId;
                    entryIndex = i;
                }
            }
        }
        return max == -1 ? successor : fingerTable.get(entryIndex);
    }

    void stabilize() {
        String message = successor.ip + " " + successor.port;
        PeerInfo successorCandidate = RPC.findPredecessor(message);
        if (successorCandidate == null) {
            return;
        }
        int candidateId = successorCandidate.id;
        if (id > successor.id) {
            if (candidateId > id || candidateId < successor.id) {
                successor = successorCandidate;
            }
        } else {
            if (id < candidateId && candidateId < successor.id) {
                successor = successorCandidate;
            }
        }
    }

    void notifySuccessor() {
        String message = id + " " + ip + " " + port + " " + successor.ip + " " + successor.port;
        RPC.notifySuccessor(message);
    }

    void fix_finger() {

    }

    void check_predecessor() {

    }

    void updateFingerTable(int index, PeerInfo peerInfo) {
        fingerTable.set(index, peerInfo);
    }

    private int getHash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(key.getBytes());
            System.out.println();
            byte b = bytes[bytes.length - 1];
            int num = ((int)b) & 15;
            return num;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void fixPredecessor(int peerId, String peerIp, int peerPort) {
        PeerInfo peerInfo = new PeerInfo(peerId, peerIp, peerPort);
        System.out.println("I am here on port " + port);
        if (successor.id == id) { // Local peer is the first peer in the ring and currently there are only two peers in the ring.
            successor = peerInfo;
            predecessor = peerInfo;
        } else if (predecessor == null) {
            System.out.println("I am here to fix predecessor on port " + port);
            predecessor = peerInfo;
        } else if (predecessor.id > id && (peerId < id || peerId > predecessor.id)) {
            predecessor = peerInfo;
        } else if (predecessor.id < peerId && peerId < id) {
            predecessor = peerInfo;
        }
    }
}

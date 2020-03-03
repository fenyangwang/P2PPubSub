import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Peer implements PubSub {
    public static final int TOTAL_NUM = 10;

    int id;
    InetAddress inetAddress;
    String ip;
    int port;
    public final static int M = 4;
    public static final int MAXTTL = 5;
    //private static final String bootIp = "172.31.4.36"; // EC2
    //private static final String bootIp = "172.31.144.91"; // XHG
    //private static final String bootIp = "172.31.134.108"; // WFY
    // private static final String bootIp = "192.168.0.16"; // WFY Home
    private static final String bootIp = "127.0.0.1"; // Dewen 

    private static final int bootPort = 8001;
    private PeerInfo bootPeer = new PeerInfo(getHash(bootIp + ":" + bootPort),bootIp, bootPort);
    private List<PeerInfo> neiList;
    private List<PeerAddress> fingerTable;
    Set<Category> validCategorySet;
    Set<Category> subscriptionList;
    private Set<Message> processedMsgSet;
    private PeerInfo predecessor;
    private PeerInfo successor;
    private Listener listener;
    private FingersFixer fingersFixer;
    private PredecessorFixer predecessorFixer;
    private Stabilizer stabilizer;
    private PredecessorChecker predecessorChecker;

    public Peer(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.ip = inetAddress.getHostAddress();
        this.port = port;
        this.id = getHash(ip + ":" + port);

        this.neiList = new ArrayList<>();
        this.fingerTable = new ArrayList<>();
        this.validCategorySet = new HashSet<>();
        // default valid categories: CAT, DOG, BIRD, RABBIT
        this.validCategorySet.add(new Category("CAT"));
        this.validCategorySet.add(new Category("DOG"));
        this.validCategorySet.add(new Category("BIRD"));
        this.validCategorySet.add(new Category("RABBIT"));
        this.subscriptionList = new HashSet<>();
        this.processedMsgSet = new HashSet<>();

        this.listener = new Listener(this);
        this.fingersFixer = new FingersFixer(this);
        this.predecessorFixer = new PredecessorFixer(this);
        this.stabilizer = new Stabilizer(this);
        this.predecessorChecker = new PredecessorChecker(this);
        new Thread(listener).start();

        create();
    }

    void create() {
        predecessor = null;
        System.out.println("new node with id: " + id + " ip: " + ip + " port: " + port + " is created");
        successor = new PeerInfo(id, ip, port);
        for (int i = 0; i < M; i++) {
            neiList.add(null);
            fingerTable.add(null);
        }
    }

    void join() {
        if (id == bootPeer.id) {
            System.out.println("The first peer has already joined.");
            new Thread(fingersFixer).start();
            new Thread(predecessorFixer).start();
            new Thread(stabilizer).start();
            new Thread(predecessorChecker).start();
            return;
        }
        predecessor = null;
        successor = RPC.requestObj(bootPeer, new Request(new PeerInfo(-1, "", -1), id + " findSucc"));
        
        updateFingerTable(0, successor);
        System.out.println("Peer with id: " + id + " ip: " + ip + " port: " + port + " has successor: ");
        System.out.println("succ id: " + successor.id);
        System.out.println("succ id: " + successor.ip);
        System.out.println("succ id: " + successor.port);
        System.out.println("succ subscriptionList list size: " + successor.subscriptionList.size());
        System.out.println();
        new Thread(fingersFixer).start();
        new Thread(predecessorFixer).start();
        new Thread(stabilizer).start();
        new Thread(predecessorChecker).start();
    }

    PeerInfo getSuccessor() {
       return successor;
    }

    PeerInfo getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(PeerInfo predecessor) {
        this.predecessor = predecessor;
    }

    public void setSuccessor(PeerInfo successor) {
        this.successor = successor;
    }

    PeerInfo findSuccessor(int targetId) {
        if (id == successor.id) {
            return successor;
        }
        if (id == targetId) {
            return new PeerInfo(id, ip, port, predecessor == null ? null : predecessor.getAddress(), successor == null ? null : successor.getAddress(), subscriptionList, fingerTable);
        }
        if (id > successor.id) {
            if (targetId > id || targetId <= successor.id) {
                return successor;
            }
        } else if (id < successor.id && id < targetId && targetId <= successor.id) {
            return successor;
        }
        PeerInfo closestPrecedingPeer = getClosetPrecedingPeer(targetId);
        return RPC.requestObj(closestPrecedingPeer, new Request(new PeerInfo(-1, "", -1), targetId + " findSucc"));
    }

    PeerInfo getClosetPrecedingPeer(int peerId) {
        int max = -1;
        int entryIndex = -1;
        for (int i = M - 1; i >= 0; i--) {
            PeerInfo peerInfo = neiList.get(i);
            if (peerInfo == null) {
                continue;
            }
            int entryId = neiList.get(i).id;
            if (entryId <= peerId) {
                if (entryId > max) {
                    max = entryId;
                    entryIndex = i;
                }
            }
        }
        return max == -1 ? successor : neiList.get(entryIndex);
    }

    void stabilize() {
        checkSuccessor();
        PeerInfo successorCandidate = RPC.requestObj(successor, new Request(new PeerInfo(-1, "", -1), "findPre"));
        if (successorCandidate == null) {
            return;
        }
        int candidateId = successorCandidate.id;
        if (id == candidateId) {
            successor = RPC.requestObj(successor, new Request(new PeerInfo(-1, "", -1), successor.id + " findSucc"));
            return;
        }
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

    public void checkPredecessor() {
        if (predecessor == null) {
            return;
        }
        boolean isAlive = RPC.isPeerAlive(predecessor);
        if (!isAlive) {
            PeerAddress newPredecessor = predecessor.predecessorAddress;
            predecessor = new PeerInfo(newPredecessor.getId(), newPredecessor.getIp(), newPredecessor.getPort());
        }
    }

    public void checkSuccessor() {
        if (successor == null) {
            return;
        }
        boolean isAlive = RPC.isPeerAlive(successor);
        if (!isAlive) {
            PeerAddress newSuccessor = successor.successorAddress;
            if (newSuccessor != null) {
                successor = new PeerInfo(newSuccessor.getId(), newSuccessor.getIp(), newSuccessor.getPort());
            }
        }
    }

    void notifySuccessor() {
        checkSuccessor();
        PeerInfo peerInfo = new PeerInfo(id, ip, port, predecessor == null ? null : predecessor.getAddress(), 
                                            successor == null ? null : successor.getAddress(), subscriptionList, fingerTable);
        String errMsg = "Successor might be offline, printed from RPC";
        RPC.sendObject(successor, new Request(peerInfo, "notify"), errMsg);
    }

    void updateFingerTable(int index, PeerInfo peerInfo) {
        neiList.set(index, peerInfo);
        fingerTable.set(index, peerInfo == null ? null : peerInfo.getAddress());
    }

    void updateSubscriptionList(PeerInfo peerInfo) {
        List<PeerInfo> newNeiList = neiList.stream()
                .map(o -> o.id == peerInfo.id ? peerInfo : o)
                .collect(Collectors.toList());
        Collections.copy(neiList, newNeiList);
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

    public void fixPredecessor(PeerInfo peerInfo) {
        // System.out.println("I am here on port " + port);
        if (successor.id == id) { // Local peer is the first peer in the ring and currently there are only two peers in the ring.
            successor = peerInfo;
            predecessor = peerInfo;
        } else if (predecessor == null) {
            // System.out.println("I am here to fix predecessor on port " + port);
            predecessor = peerInfo;
        } else if (predecessor.id > id && (peerInfo.id < id || peerInfo.id >= predecessor.id)) {
            predecessor = peerInfo;
        } else if (predecessor.id <= peerInfo.id && peerInfo.id < id) {
            predecessor = peerInfo;
        }
    }

    // Disseminate object to neighbors 
    @Override
    public void disseminate(Request request, boolean isCategoryDiss, double gamma) {
        Message msg = request.message;
        if (msg == null || request == null) {
            return;
        }
        // If the object has already been processed, return
        if (!processedMsgSet.add(msg)) {
            System.out.println("The gossip object has been processed so discard it!");
            return;
        }
        // If the object's TTL is 0
        if (msg.decreaseTTL() == -1) {
            System.out.println("The TTL of gossip object is 0 so discard it!");
            return;
        }
        // Start to disseminate the object
        int gammaNo = (int)(TOTAL_NUM * gamma);
        Random ran = new Random();
        System.out.println("\n############## Start to desseminate the object #################");
        Set<PeerInfo> processedPeer = new HashSet<>();
        for (PeerInfo peer : neiList) {
            // Avoid sending object to the same peer multiple times and avoid send to itself
            if (!processedPeer.add(peer) || (peer.ip.equals(this.ip) && peer.port == this.port)) {
                continue;
            }
            // Send object to all neighbour subscribers
            if (!isCategoryDiss && peer.subscriptionList.contains(msg.getCategory())) {
                System.out.println("\n----------------------------------------");
                System.out.printf("Send this object (TTL: %d) to the subscriber neighbor ip: %s, port: %d\n", msg.getTTL(), peer.ip, peer.port);
                System.out.println("----------------------------------------");
                RPC.sendObject(peer, request, "");
            } else { // Gossip to the remaining neighbours
                if (ran.nextInt(TOTAL_NUM) < gammaNo) {
                    System.out.println("\n****************************************");
                    System.out.printf("Gossip this object (TTL: %d) to the neighbor ip: %s, port: %d\n",msg.getTTL(), peer.ip, peer.port);
                    System.out.println("****************************************");
                    RPC.sendObject(peer, request, "");
                }
            }
        }
        System.out.println("############## Dessemination finished ##################");
    }

    // Update the subscription list
    @Override
    public void updateSubList(List<Category> categoryList, boolean subAction) {

        PeerInfo localPeerInfo = new PeerInfo(id, ip, port, subscriptionList);

        System.out.printf("Updating local subscription list -- IP = %s, Port = %d\n", ip, port);
        for (Category c: categoryList) {
            if (subAction) {
                subscriptionList.add(c);
                System.out.println("Category " + c.toString() + " subscribed");
            } else {
                subscriptionList.remove(c);
                System.out.println("Category " + c.toString() + " unsubscribed");
            }
        }

        System.out.println("\nNotify neighbors to update their finger tables per change of current peer's subscription list ...");
        Set<PeerInfo> notifiedNeighbors = new HashSet<>();
        for (PeerInfo peer: neiList) {
            // Avoid sending object to the same peer multiple times and avoid send to itself
            if (!notifiedNeighbors.add(peer) || (peer.ip.equals(this.ip) && peer.port == this.port)) {
                continue;
            }
            System.out.printf("Notify new sub list to neighbor -- ip = %s, port = %d\n", peer.ip, peer.port);
            RPC.sendObject(peer, new Request(localPeerInfo, "updateSub"), "");
        }
        System.out.println("... Notification of Updated Subscription finished ...");
    }

    // Add the new category
    public void addCategory(List<Category> newCategoryList) {
        System.out.println("Updating local valid category set ...");
        for (Category newCategory: newCategoryList) {
            validCategorySet.add(newCategory);
        }
        this.showValidCategorySet();
        Message msg = new Message(MAXTTL, ip, port);
        disseminate(new Request(newCategoryList, msg, "updateCategory"), true, PubSub.DISS_NEW_CATEGORY_GAMMA);
    }

    // Display content in the valid Category Set
    public void showValidCategorySet() {
        System.out.println("\nThe current valid category set is: ");
        for (Category c : this.validCategorySet) {
            System.out.printf("%s, ", c);
        }
        System.out.println("\n");
    }

    // The peer quit the chord network and notify its predecessor and successor
    public void quit() {
        RPC.sendObject(successor, new Request(predecessor, "changePredecessor"), "");
        System.out.println("predecessor is changed");
        RPC.sendObject(predecessor, new Request(successor, "changeSuccessor"), "");
        System.out.println("successor is changed");
        listener.stop();
        fingersFixer.stop();
        predecessorFixer.stop();
        stabilizer.stop();
        predecessorChecker.stop();
        System.out.println("All set");
    }
}
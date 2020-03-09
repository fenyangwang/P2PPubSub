public class FingersFixer implements Runnable {
    private Peer peer;
    private int m;
    boolean startFixing;
    private static boolean verbose = true;

    public FingersFixer(Peer peer) {
        this.peer = peer;
        this.m = Peer.M;
    }

    @Override
    public void run() {
        startFixing = true;
        while (startFixing) {
            if (verbose) {
             System.out.println("\n====================================================");
                System.out.printf("I'm id: %d, ip: %s, port: %d\n", peer.id, peer.ip, peer.port);
                if (peer.getPredecessor() == null) {
                    System.out.println("predecessor: null");
                } else {
                    System.out.printf("predecessor: id: %d, ip: %s, port: %d\n", 
                                    peer.getPredecessor().id, peer.getPredecessor().ip, peer.getPredecessor().port);
                }
                if (peer.getSuccessor() != null) {
                    System.out.printf("successor: id: %d, ip: %s, port: %d\n", 
                                        peer.getSuccessor().id, peer.getSuccessor().ip, peer.getSuccessor().port);
                } else {
                    System.out.println("successor: null");
                }
            }

            for (int i = 0; i < m; i++) {
                int val = (int) (peer.id + Math.pow(2, i)) % (int)Math.pow(2, m);
                PeerInfo peerInfo = peer.findSuccessor(val);
                if (peerInfo == null) {
                    if (verbose) {
                        System.out.println("peerInfo is null, Entry " + i + " is offline, set this entry as null");
                    }
                    peer.updateFingerTable(i, null);
                } else {
                    if (verbose) {
                        System.out.println("\n------------------- Finger Table ----------------------");
                        System.out.println("entry " + i + " id: " + peerInfo.id);
                    }
                }
                if (peerInfo != null) {
                    boolean isAlive = RPC.isPeerAlive(peerInfo);
                    if (isAlive) {
                        peer.updateFingerTable(i, peerInfo);
                    } else {
                        if (verbose) {
                            System.out.println("Entry " + i + " is offline, set this entry as null");
                        }
                        peer.updateFingerTable(i, null);
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("fingerfixer is done");
    }

    public void stop() {
        startFixing = false;
    }

    public static void reverseVerbose() {
        verbose = !verbose;
    }

}

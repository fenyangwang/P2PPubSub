public class FingersFixer implements Runnable {

    private Peer peer;
    boolean startFixing;
    private int m;
    public FingersFixer(Peer peer) {
        this.peer = peer;
        this.m = Peer.M;
    }


    @Override
    public void run() {
        startFixing = true;
        // int count = 0;
        while (startFixing) {
            // System.out.println("in fingers fixer");
/*            if (peer.getPredecessor() == null) {
                 System.out.println("predecessor: null");
            } else {
                 System.out.println("predecessor: " + peer.getPredecessor().id);
            }

            if (peer.getSuccessor() != null) {
                System.out.println("successor: " + peer.getSuccessor().id);
            } else {
                System.out.println("successor: null");
            }*/

            for (int i = 0; i < m; i++) {
                int val = (int) (peer.id + Math.pow(2, i)) % (int)Math.pow(2, m);
                PeerInfo peerInfo = peer.findSuccessor(val);
                if (peerInfo == null) {
                    //System.out.println("peerInfo is null, Entry " + i + " is offline, set this entry as null");
                    peer.updateFingerTable(i, null);
                } else {
                    //System.out.println("entry " + i + " " + peerInfo.id);
                }
                if (peerInfo != null) {
                    boolean isAlive = RPC.isPeerAlive(peerInfo);
                    if (isAlive) {
                        peer.updateFingerTable(i, peerInfo);
                        //peerInfo.showDetails();
                    } else {
                        //System.out.println("Entry " + i + " is offline, set this entry as null");
                        peer.updateFingerTable(i, null);
                    }
                }

               /* try {
                    if (peerInfo != null) {
                        Socket socket = new Socket(peerInfo.ip, peerInfo.port);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(new Request(new PeerInfo(-1, "", -1), "test"));
                        socket.close();
                        peer.updateFingerTable(i, peerInfo);
                        //peerInfo.showDetails();
                    }
                } catch (IOException e) {
                    System.out.println("Entry " + i + " is offline, set this entry as null");
                    peer.updateFingerTable(i, null);
                }*/
            }
            try {
                Thread.sleep(1000);
                // System.out.println("wake up, let's start loop " + ++count);
                // if (count == 1) {
                //     System.out.println("count is " + count);
                // }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("fingerfixer is done");
    }

    public void stop() {
        startFixing = false;
    }

}

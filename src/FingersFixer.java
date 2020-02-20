public class FingersFixer implements Runnable {

    private Peer peer;
    boolean startFixing;
    public FingersFixer(Peer peer) {
        this.peer = peer;
    }


    @Override
    public void run() {
        startFixing = true;
        int count = 0;
        while (startFixing) {
            System.out.println("in fingers fixer");
            if (peer.predecessor == null) {
                System.out.println("predecessor: null");
            } else {
                System.out.println("predecessor: " + peer.predecessor.id);
            }
            System.out.println("successor: " + peer.getSuccessor().id);

            for (int i = 0; i <= 3; i++) {
                int val = (int) (peer.id + Math.pow(2, i)) % 16;
                PeerInfo peerInfo = peer.findSuccessor(val);
                System.out.println("entry " + i + " " + peerInfo.id);
                peer.updateFingerTable(i, peerInfo);
            }
            try {
                Thread.sleep(1000);
                System.out.println("wake up, let's start loop " + ++count);
                if (count == 1) {
                    System.out.println("count is " + count);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        startFixing = false;
    }

}

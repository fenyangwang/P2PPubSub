public class PredecessorChecker implements Runnable {

    Peer peer;
    boolean startChecking;

    PredecessorChecker(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {
        startChecking = true;
        while (startChecking) {
            peer.checkPredecessor();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Predecessor Checker is done.");
    }

    public void stop() {
        startChecking = false;
    }
}

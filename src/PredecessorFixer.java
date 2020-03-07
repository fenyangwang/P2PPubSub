public class PredecessorFixer implements Runnable {
    private boolean startFixing;
    Peer peer;

    PredecessorFixer(Peer peer) {
        this.peer = peer;
    }
    @Override
    public void run() {
        startFixing = true;
        while (startFixing) {
            peer.notifySuccessor();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("predecessorfixer is done");
    }

    public void stop() {
        startFixing = false;
    }
}

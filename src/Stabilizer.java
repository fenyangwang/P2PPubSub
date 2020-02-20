public class Stabilizer implements Runnable {
    private Peer peer;
    private boolean startStabilizing;
    Stabilizer(Peer peer) {
        this.peer = peer;
    }
    @Override
    public void run() {
        startStabilizing = true;
        while (startStabilizing) {
            System.out.println("in stabilizing");
            peer.stabilize();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        startStabilizing = false;
    }
}

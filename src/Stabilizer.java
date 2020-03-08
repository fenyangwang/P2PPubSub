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
            peer.stabilize();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("stabilizer is done");
    }

    public void stop() {
        startStabilizing = false;
    }
}

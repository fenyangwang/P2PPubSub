import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FingersFixer implements Runnable {

    private Peer peer;
    boolean startFixing;
    private int m;
    public FingersFixer(Peer peer) {
        this.peer = peer;
        this.m = peer.M;
    }


    @Override
    public void run() {
        startFixing = true;
        int count = 0;
        while (startFixing) {
            System.out.println("in fingers fixer");
            if (peer.getPredecessor() == null) {
                System.out.println("predecessor: null");
            } else {
                System.out.println("predecessor: " + peer.getPredecessor().id);
            }
            System.out.println("successor: " + peer.getSuccessor().id);

            for (int i = 0; i < m; i++) {
                int val = (int) (peer.id + Math.pow(2, i)) % (int)Math.pow(2, m);
                PeerInfo peerInfo = peer.findSuccessor(val);
                System.out.println("entry " + i + " " + peerInfo.id);
                //peer.updateFingerTable(i, peerInfo);
                try {
                    Socket socket = new Socket(peerInfo.ip, peerInfo.port);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(new Request(null, "test"));
                    socket.close();
                    peer.updateFingerTable(i, peerInfo);
                } catch (IOException e) {
                    System.out.println("Entry " + i + " is offline, set this entry as null");
                    peer.updateFingerTable(i, null);
                }
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
        System.out.println("fingerfixer is done");
    }

    public void stop() {
        startFixing = false;
    }

}

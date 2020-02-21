import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket socket;
    private int count;
    private Peer peer;
    ServerHandler(Socket socket, Peer peer, int count) {
        this.socket = socket;
        this.peer = peer;
        this.count = count;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        PrintWriter printWriter = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Request request = (Request) objectInputStream.readObject();
            //String line = bufferedReader.readLine();
            String line = request.message;

            if (line.endsWith("findSucc")) {
                findSuccessor(line, objectOutputStream);
            } else if (line.endsWith("notify")) {
                notify(request.peerInfo);
            } else if (line.endsWith("findPre")) {
                findPredecessor(objectOutputStream);
            } else {
                sendMessage(printWriter);
            }
        } catch (IOException e) {
            System.out.println("here is the error on port " + peer.port);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findSuccessor(String line, ObjectOutputStream objectOutputStream) {
        String[] request = line.split(" ");
        int targetId = Integer.valueOf(request[0]);
        PeerInfo peerInfo = peer.findSuccessor(targetId);
        //System.out.println(peerInfo.id + " " + peerInfo.ip + " " + peerInfo.port + " " + peerInfo.subscriptionList.size());
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(peerInfo);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void notify(PeerInfo peerInfo) {
        peer.fixPredecessor(peerInfo);
    }

    private void findPredecessor(ObjectOutputStream objectOutputStream) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(peer.getPredecessor());
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(PrintWriter printWriter) {
        try {
            printWriter = new PrintWriter(socket.getOutputStream());
            System.out.println("999999999999999");
            printWriter.println("server " + count + " received message" + "on port: " + peer.port);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

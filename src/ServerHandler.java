import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerHandler implements Runnable {
    private Socket socket;
    private Peer peer;

    ServerHandler(Socket socket, Peer peer, int count) {
        this.socket = socket;
        this.peer = peer;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            Request request = (Request) objectInputStream.readObject();
            String line = request.command;
            if (line.endsWith("findSucc")) {
                findSuccessor(line, objectOutputStream);
            } else if (line.endsWith("notify")) {
                notify(request.peerInfo);
            } else if (line.endsWith("findPre")) {
                findPredecessor(objectOutputStream);
            } else if (line.endsWith("disseminateMsg")) {
                extractMessage(request);
            } else if (line.endsWith("changePredecessor")) {
                changePredecessor(request.peerInfo);
            } else if (line.endsWith("changeSuccessor")) {
                changeSuccessor(request.peerInfo);
            // } else if (line.endsWith("updateSub")) {
            //     updateNeighborSub(request.peerInfo);
            } else if (line.endsWith("updateCategory")) {
                updateCategory(request.categories, request.message);
            } else if (line.endsWith("getValidCategorySet")) {
                respondValidCategorySet(objectOutputStream);
            }
        } catch (IOException e) {
            System.out.println("here is the error on port " + peer.port);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // send valid category set
    private void respondValidCategorySet(ObjectOutputStream objectOutputStream) {
        respondObject(objectOutputStream, new PeerInfo(this.peer.validCategorySet, -1, "", -1));
    }
    
    //extract the published message, call corresponding method
    private void extractMessage(Request request) {
        Message msg = request.message;
        System.out.printf("\nReceived the message from %s, with content: %s, category: %s, created by: %s, created at: %d, ttl is %d.\n", 
                            this.socket.getRemoteSocketAddress(), msg.getContent(), msg.getCategory().toString(), msg.getSenderIP(), msg.getTimeStamp(), msg.getTTL());
        if (peer.subscriptionList.contains(msg.getCategory())) {
            System.out.println("!!! I'm the subscriber of this message !!!");
        }
        peer.disseminate(new Request(msg, "disseminateMsg"), false, PubSub.DISS_MSG_GAMMA);
    }

    private void findSuccessor(String line, ObjectOutputStream objectOutputStream) {
        String[] request = line.split(" ");
        int targetId = Integer.valueOf(request[0]);
        PeerInfo peerInfo = peer.findSuccessor(targetId);
        respondObject(objectOutputStream, peerInfo);
    }

    private void notify(PeerInfo peerInfo) {
        peer.fixPredecessor(peerInfo);
    }

    private void findPredecessor(ObjectOutputStream objectOutputStream) {
        respondObject(objectOutputStream, peer.getPredecessor());
    }

    private void changePredecessor(PeerInfo peerInfo) {
        peer.setPredecessor(peerInfo);
    }

    private void changeSuccessor(PeerInfo peerInfo) {
        peer.setSuccessor(peerInfo);
    }

    // private void updateNeighborSub(PeerInfo peerInfo) {
    //     System.out.printf("\nNotification received to update subList of Peer: Id = %d, IP = %s, Port = %d\n", peerInfo.id, peerInfo.ip, peerInfo.port);
    //     peer.updateSubscriptionList(peerInfo);
    // }

    private void updateCategory(List<Category> newCategoryList, Message msg) {
        System.out.printf("\nNotification received from %s (TTL: %d) to update valid category set\n", this.socket.getRemoteSocketAddress(), msg.getTTL());
        peer.validCategorySet.addAll(newCategoryList);
        peer.showValidCategorySet();
        peer.disseminate(new Request(newCategoryList, msg, "updateCategory"), true, PubSub.DISS_NEW_CATEGORY_GAMMA);
    }

    // Respond object through socket
    private void respondObject(ObjectOutputStream objectOutputStream, Object obj) {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

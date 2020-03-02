import java.io.*;
import java.net.BindException;
import java.net.Socket;

public class RPC {
    
    public static PeerInfo findSuccessor(String message) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        PeerInfo successorInfo = null;
        String[] request = message.split(" ");
        int targetId = Integer.valueOf(request[0]);
        String desIp = request[1];
        int desPort = Integer.valueOf(request[2]);
        try {
            socket = new Socket(desIp, desPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(new PeerInfo(-1, "", -1), targetId + " findSucc"));
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            successorInfo = (PeerInfo) objectInputStream.readObject();
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            return successorInfo;
        } catch (BindException e) {
            System.out.println("error happans when creating socket with port: " + desPort);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("destination ip is closed, return null");
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void notifySuccessor(PeerInfo peerInfo, String desIp, int desPort) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            socket = new Socket(desIp, desPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(peerInfo, "notify"));
            objectOutputStream.flush();

            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Successor might be offline, from RPC");
        }
    }

    public static PeerInfo findPredecessor(String message) {
        String[] request = message.split(" ");
        String desIp = request[0];
        int desPort = Integer.parseInt(request[1]);
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        PeerInfo predecessorInfo = null;
        try {
            socket = new Socket(desIp, desPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(new PeerInfo(-1, "", -1), "findPre"));
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            predecessorInfo = (PeerInfo) objectInputStream.readObject();

            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
            return predecessorInfo;
        } catch (IOException e) {
            System.err.println("Predecessor might be offline, from RPC.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Send object by TCP
    public static void sendObject(PeerInfo peerInfo, Request request) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(peerInfo.ip,peerInfo.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
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

    public static void notifySuccessorChangePredecessor(PeerInfo predecessor, PeerInfo successor) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(successor.ip, successor.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(predecessor, "changePredecessor"));
            objectOutputStream.flush();

            objectOutputStream.close();
            socket.close();

            System.out.println("predecessor is changed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void notifyPredecessorChangeSuccessor(PeerInfo successor, PeerInfo predecessor) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(predecessor.ip, predecessor.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(successor, "changeSuccessor"));
            objectOutputStream.flush();

            objectOutputStream.close();
            socket.close();

            System.out.println("successor is changed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPeerAlive(PeerInfo peerInfo) {
        try {
            Socket socket = new Socket(peerInfo.ip, peerInfo.port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(new PeerInfo(-1, "", -1), "test"));
            objectOutputStream.close();
            socket.close();
            return true;
        } catch (IOException e) {
            System.out.println("can't reach peer " + peerInfo.id);
            return false;
        }
    }

    public static void notifyNeighborUpdateSub(PeerInfo currentPeer, PeerInfo neighborPeer) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            socket = new Socket(neighborPeer.ip, neighborPeer.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(currentPeer, "updateSub"));
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
}

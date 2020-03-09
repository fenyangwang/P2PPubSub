import java.io.*;
import java.net.Socket;

public class RPC {
    // Request object by socket
    public static PeerInfo requestObj(PeerInfo peerInfo, Request request) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        PeerInfo returnedInfo = null;
        try {
            socket = new Socket(peerInfo.ip, peerInfo.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            returnedInfo = (PeerInfo) objectInputStream.readObject();
            return returnedInfo;
        } catch (IOException e) {
            System.err.printf("Node %d of %s : %d might be offline, printed from RPC." 
                                ,peerInfo.id, peerInfo.ip, peerInfo.port);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
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
        return null;
    }
    
    // Send object by socket
    public static void sendObject(PeerInfo peerInfo, Request request, String errMsg) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(peerInfo.ip,peerInfo.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.out.println(errMsg);
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

    // Listen and test if peer alive
    public static boolean isPeerAlive(PeerInfo peerInfo) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(peerInfo.ip, peerInfo.port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(new PeerInfo(-1, "", -1), "test"));
            objectOutputStream.flush();
            return true;
        } catch (IOException e) {
            System.out.println("can't reach peer " + peerInfo.id);
            return false;
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

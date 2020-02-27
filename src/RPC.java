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
        } catch (BindException e) {
            System.out.println("error happans when creating socket with port: " + desPort);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                objectInputStream.close();
                socket.close();
                return successorInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                socket.close();
                return predecessorInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // Send object message by TCP
    public static void sendMessage(PeerInfo peerInfo, Message msg) {
        try {
            // System.out.println("RPC is sending message or object");
            Socket socket = new Socket(peerInfo.ip,peerInfo.port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectOutputStream.writeObject(new Request(msg, "disseminate"));
            objectOutputStream.flush();

            String line = bufferedReader.readLine();
            if (line != null) {
                System.out.println(line + " from RPC on port " + peerInfo.port);
            }
            objectOutputStream.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                socket.close();
                System.out.println("predecessor is changed");
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
                socket.close();
                System.out.println("successor is changed");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void deletePeerFromFingerTable(int id) {

    }
}

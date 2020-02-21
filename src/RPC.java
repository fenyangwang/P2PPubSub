import java.io.*;
import java.net.BindException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
            objectOutputStream.writeObject(new Request(null, targetId + " findSucc"));
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
            objectOutputStream.writeObject(new Request(null, "findPre"));
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return predecessorInfo;
        }
    }


    public static void sendMessage(int port, String message) {
        try {
            System.out.println("RPC is sending " + message + " to port " + port);
            Socket socket = new Socket("127.0.0.1",port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectOutputStream.writeObject(new Request(null, "test hahahaha"));
            objectOutputStream.flush();

            System.out.println("11111");
            String line = bufferedReader.readLine();
            System.out.println("22222");

            System.out.println(line + " from RPC on port " + port);

            System.out.println("33333");

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
            socket = new Socket(successor.ip, successor.id);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(predecessor, "changePredecessor"));
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

    public static void notifyPredecessorChangeSuccessor(PeerInfo successor, PeerInfo predecessor) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(predecessor.ip, predecessor.id);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Request(successor, "changeSuccessor"));
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

    public static void deletePeerFromFingerTable(int id) {

    }
}

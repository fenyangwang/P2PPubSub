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
        PrintWriter printWriter = null;
        ObjectInputStream objectInputStream = null;
        PeerInfo successorInfo = null;
        String[] request = message.split(" ");
        int targetId = Integer.valueOf(request[0]);
        String desIp = request[1];
        int desPort = Integer.valueOf(request[2]);
        try {
            socket = new Socket(desIp, desPort);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(targetId + " findSucc");
            printWriter.flush();

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
                printWriter.close();
                objectInputStream.close();
                socket.close();

                return successorInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void notifySuccessor(String message) {
        Socket socket = null;
        PrintWriter printWriter = null;

        String[] request = message.split(" ");
        String desIp = request[3];
        int desPort = Integer.parseInt(request[4]);

        int localId = Integer.parseInt(request[0]);
        String localIp = request[1];
        int localPort = Integer.parseInt(request[2]);

        try {
            socket = new Socket(desIp, desPort);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(localId + " " + localIp + " " + localPort + " notify");
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

    public static PeerInfo findPredecessor(String message) {
        String[] request = message.split(" ");
        String desIp = request[0];
        int desPort = Integer.parseInt(request[1]);
        Socket socket = null;
        PrintWriter printWriter = null;
        ObjectInputStream objectInputStream = null;
        PeerInfo predecessorInfo = null;
        try {
            socket = new Socket(desIp, desPort);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("findPre");
            printWriter.flush();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            predecessorInfo = (PeerInfo) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
            try {
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
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            printWriter.flush();

            System.out.println("11111");
            String line = bufferedReader.readLine();
            System.out.println("22222");

            System.out.println(line + " from RPC on port " + port);

            System.out.println("33333");

            printWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

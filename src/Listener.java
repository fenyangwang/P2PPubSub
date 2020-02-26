import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
    private String ip;
    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;
    private Peer peer;
    private Socket socket;
    private boolean startListening;

    Listener(Peer peer) {
        this.peer = peer;
        this.ip = peer.inetAddress.getHostAddress();
        this.port = peer.port;
        try {
            //serverSocket = new ServerSocket(this.port);
            serverSocket = new ServerSocket(this.port, 50, inetAddress);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("server on port " + port + " is running");
        startListening = true;
        int i = 0;
        while (startListening) {
            try {
                socket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(socket, peer, i);
                Thread thread = new Thread(serverHandler);
                thread.start();
                i++;
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("listener is closed");
            }
        }
        System.out.println("listener is done");
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startListening = false;
    }

}

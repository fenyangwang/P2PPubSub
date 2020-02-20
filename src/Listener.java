import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
    private static String ip;
    private static int port;
    private ServerSocket serverSocket;
    private static Peer peer;
    private Socket socket;

    Listener(Peer peer) {
        this.peer = peer;
        this.ip = peer.ip;
        this.port = peer.port;
        try {
            serverSocket = new ServerSocket(this.port);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("server on port " + port + " is running");

        int i = 0;
        while (true) {
            try {
                socket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(socket, peer, i);
                Thread thread = new Thread(serverHandler);
                thread.start();
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

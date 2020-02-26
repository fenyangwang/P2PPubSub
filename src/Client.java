import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client{

    public static void main(String[] args) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Peer p = new Peer(inetAddress, 8001);// boot peer
        //Peer p = new Peer(inetAddress, 8002);
        //Peer p = new Peer(ip, 8003);
        //Peer p = new Peer(ip, 8004);
        //Peer p = new Peer(ip, 8005);
        //Peer p = new Peer(ip, 8006);
        //Peer p = new Peer(ip, 8007);
        //Peer p = new Peer(ip, 8008);
        //Peer p = new Peer(ip, 8009);

        p.join();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            if (message.equals("q")) {
                p.quit();
                break;
            }
            //p.sendMessage(8002, message);
            //p.sendMessage(8001, message);
        }
        System.out.println("skr");

    }

}

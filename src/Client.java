import java.util.Scanner;

public class Client implements Runnable{

    public static void main(String[] args) {
        //Peer p = new Peer("127.0.0.1", 8001);// boot peer
        //Peer p = new Peer("127.0.0.1", 8002);
        //Peer p = new Peer("127.0.0.1", 8003);
        //Peer p = new Peer("127.0.0.1", 8004);
        //Peer p = new Peer("127.0.0.1", 8005);
        //Peer p = new Peer("127.0.0.1", 8006);
        //Peer p = new Peer("127.0.0.1", 8007);
        //Peer p = new Peer("127.0.0.1", 8008);
        Peer p = new Peer("127.0.0.1", 8009);

        p.join();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            if (message.equals("quit")) {
                p.quit();
            }
            //p.sendMessage(8002, message);
            //p.sendMessage(8001, message);
        }
    }

    @Override
    public void run() {

    }
}

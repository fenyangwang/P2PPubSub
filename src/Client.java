import java.util.HashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client{

    public static final String CATEGORY = "-category";
    public static final String CONTENT = "-content";
    public static final int MAXTTL = 5;
    public static final int PORT = 8001;
    public static void main(String[] args) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Peer p = new Peer(inetAddress, PORT);
        // Peer p = new Peer("127.0.0.1", 8001);// boot peer
        // Peer p = new Peer("127.0.0.1", 8002);
        // Peer p = new Peer("127.0.0.1", 8003);
        // Peer p = new Peer("127.0.0.1", 8004);
        // Peer p = new Peer("127.0.0.1", 8005);
        // Peer p = new Peer("127.0.0.1", 8006);
        // Peer p = new Peer("127.0.0.1", 8007);
        // Peer p = new Peer("127.0.0.1", 8008);
        // Peer p = new Peer("127.0.0.1", 8009);
        p.join();
        
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("q")) {
                    p.quit();
                    break;
                } else if (command.startsWith("publish")) {
                    HashMap<String, String> commands;
                    if ((commands = parsePubCommand(command)) != null) {
                        Message msg = new Message(commands.get(CONTENT), Category.valueOf(commands.get(CATEGORY)), 
                                                    MAXTTL, inetAddress.toString(), PORT);
                        p.disseminate(msg);
                    }
                } else if (command.startsWith("subscribe")) {
                    // TODO
                    // use command format like 'subscribe -category cat' and unsubscribe -category cat 
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
        System.out.println("skr");

    }

    static private HashMap<String, String> parsePubCommand(String commandStr) {
        HashMap<String, String> commandsMap = new HashMap<>();
        String[] words = commandStr.split(" ");
        commandsMap.put(CATEGORY, "");
        commandsMap.put(CONTENT, "");

        // If user does not input 4 command line arguments, exit and display the correct command line format
        if (words.length != 5) {
            System.out.println("Lack arguments: arguments must be given as: publish -category ... -content ....");
            return null;
        }
        for (int i = 1; i < 4; i = i + 2) {
            // If user does not input command line arguments in correct format, exit and display the correct command line format
            if (!commandsMap.containsKey(words[i])) {
                System.out.println("Invalid command: arguments must be given as: publish -category ... -content ....");
                return null;
            }
            commandsMap.put(words[i], words[i + 1]);
        }
        commandsMap.put(CATEGORY, (commandsMap.get(CATEGORY)).toUpperCase());
        System.out.println("User input message category: " + commandsMap.get(CATEGORY));
        System.out.println("User input message content: " + commandsMap.get(CONTENT));

        return commandsMap;
    }
}

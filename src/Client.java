import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Client{

    public static final String CATEGORY = "-category";
    public static final String CONTENT = "-content";
    public static final String CATEGORY_SET = "-categoryset";
    public static final String SUBSCRIPTION_LIST = "-subscriptionlist";

    public static final int MAXTTL = 5;
    private static int port;
    private static final int DEFAULT_PORT = 8001;
    public static void main(String[] args) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost(); 
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // Get argument port from user input
        port = parseInputForPort(args);
        Peer p = new Peer(inetAddress, port);// boot peer 4: 1, 7 / 7 7 8 12
        // Peer p = new Peer(inetAddress, 8002);// 1: 0, 4 / 4 4 7 9
        // Peer p = new Peer(inetAddress, 8003); // 11: 9, 12 / 12 15 15 4
        // Peer p = new Peer(inetAddress, 8004);// 12: 11, 15 / 15 15 0 4
        // Peer p = new Peer(inetAddress, 8005); // 15: 12, 0 / 0 1 4 7
        //Peer p = new Peer(inetAddress, 8006); // 9: 8, 11 / 11 11 15 1
        //Peer p = new Peer(inetAddress, 8010); // 8: 7. 9 / 9 11 12 0
        //Peer p = new Peer(inetAddress, 8011); // 7: 4, 8 / 8 9 11 15
        //Peer p = new Peer(inetAddress, 8012); // 0: 15, 1 / 1 4 4 8
        p.join();
        
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("q") || command.equals("Q")) { // node quits
                    p.quit();
                    break;
                } else if (command.startsWith("publish")) {
                    publishHandler(command, p);
                } else if (command.startsWith("subscribe") || command.startsWith("unsubscribe")) {
                    subscribeHandler(command, p);
                } else if (command.startsWith("addcategory")) {
                    addCategoryHandler(command, p);
                } else if (command.startsWith("v") || command.equals("V")) {// enable or disable verbose mode
                    FingersFixer.reverseVerbose();
                } else if (command.startsWith("show")) {// show subscription list or valid category set
                    showHandler(command, p);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
        System.out.println("Done and exit!");
    }

    // Handle input command of publish
    static private void publishHandler(String command, Peer p) {
        HashMap<String, String> commands;
        if ((commands = parsePubCommand(command)) != null) {
            Message msg = new Message(commands.get(CONTENT), new Category(commands.get(CATEGORY)),
                                        MAXTTL, p.ip, port);
            // If the input category is not in the valid category set
            Category inputCategory = new Category(commands.get(CATEGORY));
            if (!p.validCategorySet.contains(inputCategory)) {
                System.out.println("The input category does not exist in the valid category set.");
                return;
            }
            System.out.printf("Preparing to disseminate the message with content: %s, category: %s, created by: %s : %d, created at: %d, ttl is %d.\n", 
                                msg.getContent(), msg.getCategory().toString(), msg.getSenderIP(), msg.getSenderPort(), msg.getTimeStamp(), msg.getTTL());
            p.disseminate(new Request(msg, "disseminateMsg"), false, PubSub.DISS_MSG_GAMMA);
        }
    }

    // Handle input command of subscribe or unsubscribe
    static private void subscribeHandler(String command, Peer p) {
        // True - to add, False - to remove
        boolean subAction = command.startsWith("subscribe") ? true : false;
        List<Category> categoryList = parseSubCommand(command, p);
        if (categoryList != null) {
            for (Category c : categoryList) {
                // See if the input category is within the valid category set
                if (!p.validCategorySet.contains(c)) {
                    categoryList.remove(c);
                    System.out.println("Invalid category: " + c.toString() + ", not found in current valid categories!");
                }
                // See whether the input category has not been subscribed but the command is "unsubscribe"
                if (!subAction && !p.subscriptionList.contains(c)) {
                    categoryList.remove(c);
                    System.out.println("Invalid category: " + c.toString() + ", unsubscribe category which is not on the subscribe list!");
                }
            }
            if (categoryList.size() > 0) {
                p.updateSubList(categoryList, subAction);
            }
        }
    }

    // Handle input command of addcategory
    static private void addCategoryHandler(String command, Peer p) {
        String[] cmdArgs = command.split(" ");
        List<Category> newCategoryList = new ArrayList<>();
        for (String name : Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length)) {
            Category c = new Category(name);
            if (!p.validCategorySet.contains(c)) {
                newCategoryList.add(c);
            }
        }
        if (newCategoryList.size() == 0) {
            System.out.println("All of the input categories already exist!");
            return;
        }
        p.addCategory(newCategoryList);
    }

    // Handle input command of show information
    static private void showHandler(String command, Peer p) {
        String[] cmdArgs = command.split(" ");
        if (cmdArgs.length != 2) {
            System.out.println("Incorrect arguments: command must be given as: show -categoryset or show -subscriptionlist!");
            return;
        }
        if (cmdArgs[1].equals(CATEGORY_SET)) {
            p.showValidCategorySet();
        } else if (cmdArgs[1].equals(SUBSCRIPTION_LIST)) {
            p.showSubscriptionList();
        } else {
            System.out.println("Incorrect arguments: command must be given as: show -categoryset or show -subscriptionlist!");
        }
    }

    // Parse the input command for publish
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

    // Parse the input command for subscribe
    static private List<Category> parseSubCommand(String cmdString, Peer peer) {
        List<Category> categoryList = new LinkedList<>();
        String[] args = cmdString.split(" ");
        if ( (args.length < 3) || (!args[1].equals(CATEGORY)) ) {
            System.out.println("Incorrect arguments: command must be given as: subscribe -category cat");
            return null;
        } else {
            for (String arg : Arrays.copyOfRange(args, 2, args.length)) {
                categoryList.add(new Category(arg));
            }
            return categoryList;
        }
    }

    // Parse the input command for port
    static private int parseInputForPort(String[] cmdArgs) {
        if (cmdArgs == null || cmdArgs.length < 2 || !cmdArgs[0].toLowerCase().equals("-port")) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(cmdArgs[1]);
    }
}

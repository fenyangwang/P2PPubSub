import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Client{

    public static final String CATEGORY = "-category";
    public static final String CONTENT = "-content";
    public static final int MAXTTL = 5;
    public static final int PORT = 8001;
    public static void main(String[] args) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost(); // 172.31.144.91
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Peer p = new Peer(inetAddress, PORT);// boot peer 4: 1, 7 / 7 7 8 12
        //Peer p = new Peer(inetAddress, 8002);// 1: 0, 4 / 4 4 7 9
        //Peer p = new Peer(inetAddress, 8003); // 11: 9, 12 / 12 15 15 4
        //Peer p = new Peer(inetAddress, 8004);// 12: 11, 15 / 15 15 0 4
        //Peer p = new Peer(inetAddress, 8005); // 15: 12, 0 / 0 1 4 7
        //Peer p = new Peer(inetAddress, 8006); // 9: 8, 11 / 11 11 15 1
        //Peer p = new Peer(inetAddress, 8010); // 8: 7. 9 / 9 11 12 0
        //Peer p = new Peer(inetAddress, 8011); // 7: 4, 8 / 8 9 11 15
        //Peer p = new Peer(inetAddress, 8012); // 0: 15, 1 / 1 4 4 8
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
                        Message msg = new Message(commands.get(CONTENT), new Category(commands.get(CATEGORY)),
                                                    MAXTTL, inetAddress.toString(), PORT);
                        p.disseminate(msg);
                    }
                } else if (command.startsWith("subscribe") || command.startsWith("unsubscribe")) {
                    // use command format like 'subscribe -category cat' and unsubscribe -category cat

                    // True - to add, False - to remove
                    boolean subAction = command.startsWith("subscribe") ? true : false;
                    List<Category> categoryList = parseSubCommand(command, p);
                    if (categoryList != null) {
                        for (Category c: categoryList) {
                            if (!p.validCategorySet.contains(c)) {
                                categoryList.remove(c);
                                System.out.println("Invalid category: " + c.toString() +
                                        ", not found in current valid categories!");
                            }
                        }
                        if (categoryList.size() > 0)
                            p.updateSubList(categoryList, subAction);
                    }
                } else if (command.startsWith("addcategory")) {
                    String[] cmdArgs = command.split(" ");
                    List<Category> newCategoryList = new ArrayList<>();
                    for (String name: Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length)) {
                        Category c = new Category(name);
                        newCategoryList.add(c);
                    }
                    if (newCategoryList.size() > 0)
                        p.addCategory(newCategoryList);
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

    static private List<Category> parseSubCommand(String cmdString, Peer peer) {
        List<Category> categoryList = new ArrayList<>();
        String[] args = cmdString.split(" ");
        if ( (args.length < 3) || (!args[1].equals(CATEGORY)) ) {
            System.out.println("Incorrect arguments: command must be given as: subscribe -category cat");
            return null;
        } else {
            for (String arg: Arrays.copyOfRange(args, 2, args.length)) {
                    categoryList.add(new Category(arg));
            }
            return categoryList;
        }
    }

}

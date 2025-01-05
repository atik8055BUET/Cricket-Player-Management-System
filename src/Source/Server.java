package Source;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;
    private static ConcurrentHashMap<String, String> userDatabase = new ConcurrentHashMap<>();
    volatile public static ArrayList<Player> playerList = new ArrayList<>();
    volatile public static TreeMap<String, ArrayList<Player>> clublist = new TreeMap<String, ArrayList<Player>>();
    volatile public static ArrayList<Player> SellingplayerList = new ArrayList<>();
    volatile public static TreeMap<String, ArrayList<Player>> Sellingclublist = new TreeMap<String, ArrayList<Player>>();
    volatile public static TreeMap<String,Long> clubBalance = new TreeMap<String,Long>();
    volatile public static TreeMap<String,Boolean> AnyChangeSell = new TreeMap<String,Boolean>();
    volatile public static TreeMap<String,Boolean> AnyChangeList = new TreeMap<String,Boolean>();
    volatile public static TreeMap<String,Boolean> AlreadLogedin = new TreeMap<String,Boolean>();
    volatile public static TreeMap<String,ArrayList<Player>> otherClubList=new TreeMap<String,ArrayList<Player>>();
    volatile public static TreeMap<String,Boolean> anyChangeOtherClub=new TreeMap<String,Boolean>();
    volatile public static TreeMap<String,Boolean> newClubAdded=new TreeMap<String,Boolean>();
    final public static int changeTime=2000;
    final public static Boolean playerPhoto=true;
    final public static String DefaultPassword="123";
    final public static boolean writeFile=false;

    public static void main(String[] args) {
        Utility.initializePlayerList(userDatabase,false);
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, userDatabase).start();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

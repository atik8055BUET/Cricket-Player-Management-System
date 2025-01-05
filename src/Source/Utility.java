package Source;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Utility {
    static Scanner input = new Scanner(System.in);

    public static int takeIntInput() {
        while (!input.hasNextInt()) {
            System.out.println("Invalid input. Please Try again!!!");
            input.next();
        }
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }

    public static String takeStringInput() {
        String str = input.nextLine().trim();
        while (str.isEmpty()) {
            System.out.println("Invalid input. Please enter a non-empty string.");
            str = input.nextLine().trim();
        }
        str = capitalize(str);
        return str;
    }

    public static double takeDoubleInput() {
        while (!input.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid double.");
            input.next();
        }
        double value = input.nextDouble();
        input.nextLine();
        return value;
    }

    public static ArrayList<String> read(String INPUT_FILE_NAME) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE_NAME));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                lines.add(line);
            }
            br.close();
            return lines;
        } catch (Exception e) {
            System.out.println("File not found");
            return lines;
        }
    }

    public static String capitalize(String s) {
        if (s.isEmpty())
            return s;
        // return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        String[] parts = s.split(" ");
        String result = "";
        for (String part : parts) {
            result += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ";
        }
        return result.trim();
    }

    public static void writePlayer(ArrayList<Player> players) {
        String OUTPUT_FILE_NAME = "src\\assets\\players.txt";
        try {
            FileWriter writer = new FileWriter(OUTPUT_FILE_NAME);
            for (Player player : players) {
                writer.write(
                        player.name + "," + player.country + "," + player.age + "," + player.height + "," + player.club
                                + "," + player.position + "," + player.number + "," + player.weeklySalary + (player.state?",1\n":",0\n"));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("File not found");
        }
    }

    public static void writeClub(TreeMap<String, Long> clubs,ConcurrentHashMap<String, String> userDatabase) {
        String OUTPUT_FILE_NAME = "src\\assets\\club.txt";
        try {
            FileWriter writer = new FileWriter(OUTPUT_FILE_NAME);
            for (String club : clubs.keySet()) {
                writer.write(club + "," + clubs.get(club) + "," + userDatabase.get(club) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("File not found");
        }
    }

    public static void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void defaultClubBalance(TreeMap<String, ArrayList<Player>> clublist) {
        TreeMap<String, Long> clubBalancedefault = new TreeMap<String, Long>();
        for (String club : clublist.keySet()) {
            clubBalancedefault.put(club, 99999999L);
        }
        // Utility.writeClub(clubBalancedefault);
    }

    static void initializePlayerList(ConcurrentHashMap<String, String> userDatabase, Boolean BalanceDefault) {
        String INPUT_FILE_NAME = "src\\assets\\players.txt";
        String CLUB_FILE_NAME = "src\\assets\\club.txt";
        ArrayList<String> players = Utility.read(INPUT_FILE_NAME);
        for (String player : players) {
            Player newPlayer = new Player(player);
            Server.playerList.add(newPlayer);
            if (Server.clublist.containsKey(newPlayer.club)) {
                Server.clublist.get(newPlayer.club).add(newPlayer);
            } else {
                ArrayList<Player> temp = new ArrayList<Player>();
                temp.add(newPlayer);
                Server.clublist.put(newPlayer.club, temp);
            }

            if (newPlayer.state == true) {
                Server.SellingplayerList.add(newPlayer);
                for (String club : Server.clublist.keySet()) {
                    if (club.equals(newPlayer.club) == false) {
                        if (Server.Sellingclublist.containsKey(club)) {
                            Server.Sellingclublist.get(club).add(newPlayer);
                        } else {
                            ArrayList<Player> temp = new ArrayList<Player>();
                            temp.add(newPlayer);
                            Server.Sellingclublist.put(club, temp);
                        }
                    }
                }
            }
        }

        // userDatabase.put("admin", "123");
        // userDatabase.put("", "");
        for (String club : Server.clublist.keySet()) {
            // userDatabase.put(club, "123");
            if (Server.Sellingclublist.containsKey(club) == false) {
                Server.Sellingclublist.put(club, new ArrayList<Player>());
            }
            Server.AnyChangeSell.put(club, false);
            Server.AnyChangeList.put(club, false);
            Server.AlreadLogedin.put(club, false);
            Server.newClubAdded.put(club, false);
        }
        Server.AnyChangeSell.put("admin", false);
        Server.AnyChangeList.put("admin", false);
        Server.AlreadLogedin.put("admin", false);
        Server.newClubAdded.put("admin", false);
        if (BalanceDefault) {
            defaultClubBalance(Server.clublist);
        }
        ArrayList<String> clubBalances = Utility.read(CLUB_FILE_NAME);
        for (String clubBalance : clubBalances) {
            String[] parts = clubBalance.split(",");
            Server.clubBalance.put(parts[0], Long.parseLong(parts[1]));
            userDatabase.put(parts[0], parts[2]);
        }
        Server.clubBalance.put("admin", 0L);

        for(Player p:Server.playerList){
            for(String club:Server.clublist.keySet()){
                if(club.equals(p.club)==false){
                    Player tempPlayer = new Player(p.name,p.country,p.age,p.height,p.club,p.position,p.number,p.weeklySalary);
                    String clubOclub=club+"-"+tempPlayer.club;
                    if(Server.otherClubList.containsKey(clubOclub)){
                        Server.otherClubList.get(clubOclub).add(tempPlayer);
                    }else{
                        ArrayList<Player> temp = new ArrayList<Player>();
                        temp.add(tempPlayer);
                        Server.otherClubList.put(clubOclub, temp);
                        Server.anyChangeOtherClub.put(clubOclub, false);
                    }
                }
            }
        }
        for(String club:Server.clublist.keySet()){
            String clubOclub="admin-"+club;
            if(Server.otherClubList.containsKey(clubOclub)==false){
                Server.otherClubList.put(clubOclub, Server.clublist.get(club));
                Server.anyChangeOtherClub.put(clubOclub, false);
            }
        }

    }
}

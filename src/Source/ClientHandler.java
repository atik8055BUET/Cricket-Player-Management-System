package Source;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends Thread {
    private ConcurrentHashMap<String, String> userDatabase;
    private NetworkUtil networkUtil;

    public ClientHandler(Socket socket, ConcurrentHashMap<String, String> userDatabase) {
        this.userDatabase = userDatabase;
        try {
            this.networkUtil = new NetworkUtil(socket);
        } catch (IOException e) {
            // System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // @SuppressWarnings("null")
    public void run() {
        try {
            String work = (String) networkUtil.read();

            if (work.equals("I want to login")) {
                String username = (String) networkUtil.read();
                String password = (String) networkUtil.read();
                synchronized (userDatabase) {
                    synchronized (Server.AlreadLogedin) {
                        if (Server.AlreadLogedin.containsKey(username) && Server.AlreadLogedin.get(username) == true) {
                            networkUtil.write("Already Logged In");
                            return;
                        } else if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                            Server.AlreadLogedin.put(username, true);
                            String msg = "Login successful";
                            networkUtil.write(msg);
                        }
                    }
                }
            }

            else if (work.equals("Client Want to update PlayerList")) {
                Object input = networkUtil.read();
                if (input instanceof Player) {
                    Player newPlayer = (Player) input;
                    updatePlayerList(newPlayer);

                    synchronized(Server.otherClubList){
                        synchronized(Server.anyChangeOtherClub){
                            for(String club:Server.clublist.keySet()){
                                if(club.equals(newPlayer.club)==false){
                                    String clubOclub=club+"-"+newPlayer.club;
                                    if(Server.otherClubList.containsKey(clubOclub)){
                                        Server.otherClubList.get(clubOclub).add(newPlayer);
                                    }else{
                                        ArrayList<Player> temp = new ArrayList<Player>();
                                        temp.add(newPlayer);
                                        Server.otherClubList.put(clubOclub, temp);
                                    }
                                    Server.anyChangeOtherClub.put(clubOclub, true);
                                }
                            }
                            if(Server.writeFile)Utility.writePlayer(Server.playerList);
                        }
                    }

                }
                // Utility.writePlayer(Server.playerList);
                synchronized (Server.AnyChangeList) {
                    synchronized (Server.AnyChangeSell) {
                        for (String club : Server.AnyChangeList.keySet()) {
                            Server.AnyChangeList.put(club, true);
                        }
                        for (String club : Server.AnyChangeSell.keySet()) {
                            Server.AnyChangeSell.put(club, true);
                        }
                    }
                }
            }

            else if (work.equals("Request PlayerList Update")) {
                String username = (String) networkUtil.read();
                networkUtil.write(getPlayerList(username));
            }

            else if (work.equals("Request PlayerList Update recursive")) {

                synchronized (Server.AnyChangeList) {

                    String username = (String) networkUtil.read();

                    if (Server.AnyChangeList.get(username) == false) {
                        networkUtil.write("No Change");
                        return;
                    } else {
                        networkUtil.write("Change");
                    }

                    networkUtil.write(getPlayerList(username));

                    Server.AnyChangeList.put(username, false);

                }

            }

            else if (work.equals("Request Selling PlayerList Update")) {
                synchronized (Server.clublist) {

                    synchronized (Server.Sellingclublist) {

                        String username = (String) networkUtil.read();

                        if (Server.clublist.containsKey(username)) {
                            if (Server.Sellingclublist.containsKey(username)) {
                                networkUtil.write(Server.Sellingclublist.get(username));
                            } else {
                                networkUtil.write(new ArrayList<Player>());
                            }
                        } else {

                            if (Server.SellingplayerList.isEmpty()) {
                                networkUtil.write(new ArrayList<Player>());
                            } else {
                                networkUtil.write(Server.SellingplayerList);
                            }

                        }

                    }

                }

            }

            else if (work.equals("Request Selling PlayerList Update recursive")) {
                String username = (String) networkUtil.read();
                synchronized (Server.clublist) {

                    synchronized (Server.Sellingclublist) {

                        synchronized (Server.AnyChangeSell) {

                            if (Server.AnyChangeSell.get(username) == false) {
                                networkUtil.write("No Change");
                                return;
                            } else {
                                networkUtil.write("Change");
                            }

                            if (Server.clublist.containsKey(username)) {
                                if (Server.Sellingclublist.containsKey(username)) {
                                    networkUtil.write(Server.Sellingclublist.get(username));
                                } else {
                                    networkUtil.write(new ArrayList<Player>());
                                }
                            } else {

                                if (Server.SellingplayerList.isEmpty()) {
                                    networkUtil.write(new ArrayList<Player>());
                                } else {
                                    networkUtil.write(Server.SellingplayerList);
                                }

                            }

                            Server.AnyChangeSell.put(username, false);

                        }
                    }

                }

            }

            else if (work.equals("I want to sell")) {

                synchronized (Server.Sellingclublist) {

                    synchronized (Server.AnyChangeSell) {
                        synchronized (Server.AnyChangeList) {

                            synchronized (Server.playerList) {
                                for (String club : Server.AnyChangeSell.keySet()) {
                                    Server.AnyChangeSell.put(club, true);
                                }
                                for (String club : Server.AnyChangeList.keySet()) {
                                    Server.AnyChangeList.put(club, true);
                                }

                                // String username = (String) networkUtil.read();
                                String playername = (String) networkUtil.read();
                                for (Player player : Server.playerList) {
                                    if (player.name.equals(playername)) {

                                        if (player.state != true) {
                                            player.state = true;

                                            Server.SellingplayerList.add(player);

                                            for (String club : Server.Sellingclublist.keySet()) {
                                                if (club.equals(player.club) == false) {
                                                    if (Server.Sellingclublist.containsKey(club)) {
                                                        Server.Sellingclublist.get(club).add(player);
                                                    } else {
                                                        ArrayList<Player> temp = new ArrayList<Player>();
                                                        temp.add(player);
                                                        Server.Sellingclublist.put(club, temp);
                                                    }
                                                }
                                            }

                                        }
                                        break;
                                    }
                                }

                                if(Server.writeFile)Utility.writePlayer(Server.playerList);

                                
                            }
                        }
                    }

                }

            }

            else if (work.equals("I want to buy")) {
                synchronized (Server.clublist) {

                    synchronized (Server.Sellingclublist) {
                        synchronized (Server.clubBalance) {
                            synchronized (Server.AnyChangeSell) {
                                synchronized (Server.AnyChangeList) {

                                    synchronized (Server.playerList) {
                                        for (String club : Server.AnyChangeSell.keySet()) {
                                            Server.AnyChangeSell.put(club, true);
                                        }

                                        for (String club : Server.AnyChangeList.keySet()) {
                                            Server.AnyChangeList.put(club, true);
                                        }

                                        String username = (String) networkUtil.read();
                                        String playername = (String) networkUtil.read();
                                        Player newPlayer = null;

                                        for (Player player : Server.SellingplayerList) {
                                            if (player.name.equals(playername)) {
                                                newPlayer = player;
                                                break;
                                            }
                                        }

                                        if (newPlayer == null) {
                                            networkUtil.write("Not Available");
                                            return;
                                        }

                                        if (newPlayer.getWeeklySalary() * 52 > Server.clubBalance
                                                .get(username)) {
                                            networkUtil.write("Insufficient Balance");
                                            return;
                                        }

                                        Server.SellingplayerList.remove(newPlayer);

                                        for (String club : Server.Sellingclublist.keySet()) {
                                            if (Server.Sellingclublist.containsKey(club)
                                                    && Server.Sellingclublist.get(club).contains(newPlayer)) {
                                                Server.Sellingclublist.get(club).remove(newPlayer);
                                            }
                                        }

                                        Server.playerList.remove(newPlayer);
                                        Server.clublist.get(newPlayer.club).remove(newPlayer);

                                        Server.clubBalance.put(newPlayer.club,
                                                Server.clubBalance.get(newPlayer.club)
                                                        + newPlayer.getWeeklySalary() * 52);

                                        // add to new club
                                        newPlayer.setState(false);
                                        newPlayer.setClub(username);
                                        Server.playerList.add(newPlayer);
                                        Server.clublist.get(username).add(newPlayer);

                                        Server.clubBalance.put(username, Server.clubBalance.get(username)
                                                - newPlayer.getWeeklySalary() * 52);
                                        


                                        
                                        synchronized(Server.otherClubList){
                                            synchronized(Server.anyChangeOtherClub){
                                                for(String club:Server.clublist.keySet()){
                                                    for(String club2:Server.clublist.keySet()){
                                                        if(club.equals(club2))continue;

                                                        String clubOclub=club+"-"+club2;
                                                        Player rem=null;
                                                        for(Player p:Server.otherClubList.get(clubOclub)){
                                                            if(p.name.equals(newPlayer.name)){
                                                                rem=p;
                                                                break;
                                                            }
                                                        }
                                                        if(rem!=null){
                                                            Server.otherClubList.get(clubOclub).remove(rem);
                                                        }
                                                    }
                                                }
                                                for(String club:Server.clublist.keySet()){
                                                    if(club.equals(newPlayer.club)==false){
                                                        String clubOclub=club+"-"+newPlayer.club;
                                                        if(Server.otherClubList.containsKey(clubOclub)){
                                                            Server.otherClubList.get(clubOclub).add(newPlayer);
                                                        }else{
                                                            ArrayList<Player> temp = new ArrayList<Player>();
                                                            temp.add(newPlayer);
                                                            Server.otherClubList.put(clubOclub, temp);
                                                        }
                                                        Server.anyChangeOtherClub.put(clubOclub, true);
                                                    }
                                                }
                                            }
                                        }
                                        if(Server.writeFile){
                                            Utility.writePlayer(Server.playerList);
                                            Utility.writeClub(Server.clubBalance, userDatabase);
                                        }
                                        networkUtil.write("Player Bought");
                                        

                                    }

                                }
                            }
                        }
                    }
                }

            }
            
            else if (work.equals("Request Club Balance")) {
                synchronized (Server.clubBalance) {
                    String username = (String) networkUtil.read();
                    networkUtil.write(Server.clubBalance.get(username));
                }
            }
            else if(work.equals("I want to logout")){
                String username = (String) networkUtil.read();
                synchronized(Server.AlreadLogedin){
                    Server.AlreadLogedin.put(username, false);
                }
            }
            else if(work.equals("Change Password")){
                String username = (String) networkUtil.read();
                String oldPassword=(String) networkUtil.read();
                String newPassword=(String) networkUtil.read();
                synchronized(userDatabase){
                    if(userDatabase.containsKey(username) && userDatabase.get(username).equals(oldPassword)){
                        userDatabase.put(username, newPassword);
                        networkUtil.write("Password Changed");
                    }
                    else{
                        System.out.println("username: "+username+" oldPassword: "+oldPassword+" newPassword: "+newPassword+"Correct password: "+userDatabase.get(username));
                        networkUtil.write("Invalid Password");
                    }
                }
                if(Server.writeFile)Utility.writeClub(Server.clubBalance, userDatabase);
            }

            else if(work.equals("Give me all club name")){
                synchronized(Server.clublist){
                    networkUtil.write(new ArrayList<String>(Server.clublist.keySet()));
                }
            }

            else if(work.equals("Give me all club name recursive")){
                synchronized(Server.newClubAdded){
                    String username=(String) networkUtil.read();
                    if(Server.newClubAdded.get(username)){
                        networkUtil.write("Change");
                        synchronized(Server.clublist){
                            networkUtil.write(new ArrayList<String>(Server.clublist.keySet()));
                        }
                        Server.newClubAdded.put(username, false);
                    }else{
                        networkUtil.write("No Change");
                    }
                }
            }
            else if (work.equals("Request Other Club Player List")) {
                synchronized (Server.otherClubList) {
                    String username = (String) networkUtil.read();
                    String club = (String) networkUtil.read();
                    // if(username.equals(club)){
                    //     networkUtil.write(getPlayerList(username));
                    //     return;
                    // }
                    if(username.equals("admin")){
                        networkUtil.write(getPlayerList(club));
                        return;
                    }
                    String clubOclub = username + "-" + club ;
                    if (Server.otherClubList.containsKey(clubOclub)) {
                        networkUtil.write(Server.otherClubList.get(clubOclub));
                    } else {
                        networkUtil.write(new ArrayList<Player>());
                    }
                }
            }
            else if (work.equals("Request Other Club Player List recursive")) {
                synchronized (Server.anyChangeOtherClub) {
                    synchronized (Server.otherClubList) {
                        String username = (String) networkUtil.read();
                        String club = (String) networkUtil.read();
                        String clubOclub = username + "-" + club;
                        if (Server.anyChangeOtherClub.get(clubOclub) == false) {
                            networkUtil.write("No Change");
                            return;
                        } else {
                            networkUtil.write("Change");
                        }
                        if (Server.otherClubList.containsKey(clubOclub)) {
                            networkUtil.write(Server.otherClubList.get(clubOclub));
                        } else {
                            networkUtil.write(new ArrayList<Player>());
                        }
                        Server.anyChangeOtherClub.put(clubOclub, false);
                    }
                }
            }

            else {
                networkUtil.write("Invalid username or password");
            }

        }

        catch (Exception e) {
            // System.out.println("Error: " + ex.getMessage());
            e.printStackTrace();
        }

        finally {
            try {
                networkUtil.closeConnection();
            } catch (IOException e) {
                // System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void updatePlayerList(Player newPlayer) {
        synchronized (Server.clublist) {

            synchronized (Server.Sellingclublist) {
                synchronized (Server.clubBalance) {
                    synchronized (Server.AnyChangeSell) {
                        synchronized (Server.AnyChangeList) {
                            synchronized (userDatabase) {
                                synchronized (Server.playerList) {
                                    synchronized (Server.AlreadLogedin) {
                                        Server.playerList.add(newPlayer);

                                        if (Server.clublist.containsKey(newPlayer.getClub())) {
                                            Server.clublist.get(newPlayer.getClub()).add(newPlayer);

                                            Server.AnyChangeList.put(newPlayer.getClub(), true);

                                        } else {
                                            ArrayList<Player> newClubList = new ArrayList<>();
                                            newClubList.add(newPlayer);
                                            Server.clublist.put(newPlayer.getClub(), newClubList);

                                            Server.Sellingclublist.put(newPlayer.getClub(),
                                                    new ArrayList<Player>());

                                            Server.clubBalance.put(newPlayer.getClub(), 9999999999L);

                                            Server.AnyChangeSell.put(newPlayer.getClub(), false);

                                            Server.AnyChangeList.put(newPlayer.getClub(), false);

                                            userDatabase.put(newPlayer.getClub(), Server.DefaultPassword);
                                            synchronized(Server.newClubAdded){
                                                for(String club:Server.clublist.keySet()){
                                                    Server.newClubAdded.put(club, true);
                                                }
                                                Server.newClubAdded.put("admin", true);
                                            }

                                        }
                                        Server.AlreadLogedin.put(newPlayer.getClub(), false);
                                        Server.AnyChangeList.put("admin", true);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    ArrayList<Player> getPlayerList(String username) {
        synchronized (Server.playerList) {
            synchronized (Server.clublist) {
                if (Server.clublist.containsKey(username)) {
                    return Server.clublist.get(username);
                } else if (username.equals("admin")) {
                    return Server.playerList;
                } else {
                    return new ArrayList<Player>();
                }
            }
        }
    }
}
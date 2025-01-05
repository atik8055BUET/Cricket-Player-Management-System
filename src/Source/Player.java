package Source;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    
    String name,country,club,position;
    int age,number,weeklySalary;
    boolean state=false;
    public String getName() {
        return name;
    }
    public String getCountry() {
        return country;
    }
    public String getClub() {
        return club;
    }
    public String getPosition() {
        return position;
    }
    public int getAge() {
        return age;
    }
    public int getNumber() {
        return number;
    }
    public int getWeeklySalary() {
        return weeklySalary;
    }
    public double getHeight() {
        return height;
    }
    public boolean getState() {
        return state;
    }
    public void setClub(String club) {
        this.club = club;
    }
    public void setState(boolean state) {
        this.state = state;
    }

    double height;
    public Player(){
        this.name="";
        this.country="";
        this.club="";
        this.position="";
        this.age=0;
        this.number=0;
        this.weeklySalary=0;
        this.height=0;
        this.state=false;
    }
    public Player(String name,String country,int age,double height,String club,String position,int number,int weeklySalary){
        this.name=Utility.capitalize(name);
        this.country=Utility.capitalize(country);
        this.club=Utility.capitalize(club);
        this.position=Utility.capitalize(position);
        this.age=age;
        this.number=number;
        this.weeklySalary=weeklySalary;
        this.height=height;
        this.state=false;
    }

    public Player(String name,String country,int age,double height,String club,String position,int number,int weeklySalary,boolean state){
        this.name=Utility.capitalize(name);
        this.country=Utility.capitalize(country);
        this.club=Utility.capitalize(club);
        this.position=Utility.capitalize(position);
        this.age=age;
        this.number=number;
        this.weeklySalary=weeklySalary;
        this.height=height;
        this.state=state;
    }
    
    public Player(String s){
        try{
            String[] parts = s.split(",");
            this.name=Utility.capitalize(parts[0]);
            this.country=Utility.capitalize(parts[1]);
            this.age=Integer.parseInt(parts[2]);
            this.height=Double.parseDouble(parts[3]);
            this.club=Utility.capitalize(parts[4]);
            this.position=Utility.capitalize(parts[5]);
            if(parts[6].equals("")){
                this.number=-1;
            }
            else{
                this.number=Integer.parseInt(parts[6]);
            }
            this.weeklySalary=Integer.parseInt(parts[7]);
            this.state=parts[8].equals("1");
        }
        catch(Exception e){
            System.out.println("Invalid Input...");
        }
    }

    public static ArrayList<Player> SearchByName(String name,ArrayList<Player> playerList) {
    ArrayList<Player> result = new ArrayList<Player>();
    for (Player p : playerList) {
        if (p.name.contains(name)) {
            result.add(p);
        }
    }
    return result;
    }

    public static ArrayList<Player> SearchByCountry(String country,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.country.contains(country)) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByClub(String club,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.club.contains(club)) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByAge(int age,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.age==age) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByAge(int min,int max,ArrayList<Player> playerList) {
        min=Math.min(min,max);
        max=Math.max(min,max);
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.age>=min && p.age<=max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByHeight(double h,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.height==h) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByHeight(double min,double max,ArrayList<Player> playerList) {
        min=Math.min(min,max);
        max=Math.max(min,max);
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.height>=min && p.height<=max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByWeeklySalary(double s,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.weeklySalary==s) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByWeeklySalary(double min,double max,ArrayList<Player> playerList) {
        min=Math.min(min,max);
        max=Math.max(min,max);
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.weeklySalary>=min && p.weeklySalary<=max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByNumber(int n,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.number==n) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchByPosition(String position,ArrayList<Player> playerList) {
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : playerList) {
            if (p.position.contains(position)) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchmaxSalary(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int max=0;
        for (Player p : playerList) {
            if (p.weeklySalary>max) {
                max=p.weeklySalary;
            }
        }
        for (Player p : playerList) {
            if (p.weeklySalary==max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchmaxHeight(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        double max=0;
        for (Player p : playerList) {
            if (p.height>max) {
                max=p.height;
            }
        }
        for (Player p : playerList) {
            if (p.height==max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchmaxAge(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int max=0;
        for (Player p : playerList) {
            if (p.age>max) {
                max=p.age;
            }
        }
        for (Player p : playerList) {
            if (p.age==max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchmaxNumber(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int max=0;
        for (Player p : playerList) {
            if (p.number>max) {
                max=p.number;
            }
        }
        for (Player p : playerList) {
            if (p.number==max) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchminSalary(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int min=Integer.MAX_VALUE;
        for (Player p : playerList) {
            if (p.weeklySalary<min) {
                min=p.weeklySalary;
            }
        }
        for (Player p : playerList) {
            if (p.weeklySalary==min) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchminHeight(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        double min=Double.MAX_VALUE;
        for (Player p : playerList) {
            if (p.height<min) {
                min=p.height;
            }
        }
        for (Player p : playerList) {
            if (p.height==min) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchminAge(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int min=Integer.MAX_VALUE;
        for (Player p : playerList) {
            if (p.age<min) {
                min=p.age;
            }
        }
        for (Player p : playerList) {
            if (p.age==min) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Player> SearchminNumber(ArrayList<Player> playerList){
        ArrayList<Player> result = new ArrayList<Player>();
        int min=Integer.MAX_VALUE;
        for (Player p : playerList) {
            if (p.number<min) {
                min=p.number;
            }
        }
        for (Player p : playerList) {
            if (p.number==min) {
                result.add(p);
            }
        }
        return result;
    }

    static public void print(Player p) {
        System.out.println("Player Details of " + p.name + " :");
        System.out.println("-------------------------------------------------");
        System.out.println("Country: " + p.country);
        System.out.println("Club: " + p.club);
        System.out.println("Position: " + p.position);
        System.out.println("Age: " + p.age);
        System.out.println("Number: " + p.number);
        System.out.println("Weekly Salary: " + p.weeklySalary);
        System.out.println("Height: " + p.height);
        System.out.println("State: " + (p.state?"Playing":"Not Playing"));
        System.out.println();
    }

    static public void print(ArrayList<Player> p) {
        for (Player i : p) {
            print(i);
        }
        if (p.isEmpty()) {
            System.out.println("No Players Found!!!");
        }
    }
}

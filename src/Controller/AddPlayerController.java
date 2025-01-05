package Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import Source.NetworkUtil;
import Source.Player;
import Source.Server;
import Source.Utility;

public class AddPlayerController implements Initializable {

    @FXML
    private Button Search;

    @FXML
    private Button addButton;

    @FXML
    private TextField ageField;

    @FXML
    private Rectangle clubColour;

    @FXML
    private TextField clubField;

    @FXML
    private Label clubName;

    @FXML
    private Text clubText;

    @FXML
    private TextField countryField;

    @FXML
    private TextField heightField;

    @FXML
    private ComboBox<String> logout;

    @FXML
    private TextField nameField;

    @FXML
    private TextField numberField;

    @FXML
    private VBox playerContainer;

    @FXML
    private TextField positionField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField searchFeild;

    @FXML
    private ComboBox<String> searchFilter;

    @FXML
    private Button addPlayerButton;

    @FXML
    private Button searchPlayerButton;

    @FXML
    private Button viewPlayerButton;

    public ArrayList<Player> players = new ArrayList<Player>();
    
    private Timer timer;
    
    private Timer timer2=null;

    @FXML
    private ComboBox<String> otherClub;

    boolean isotherClub = false;

    @SuppressWarnings("unchecked")
    @FXML
    private void handleotherClub(ActionEvent event) {
        try {
            if (timer != null) {
                timer.cancel();
            }
            if(timer2!=null){
                timer2.cancel();
            }
            String club = otherClub.getValue();
            if (club.equals("My Club")) {
                isotherClub = false;
                try {
                    UpdateList();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                loadPlayerCells(players);
                startScheduledUpdate();
            } else {
                isotherClub = true;
                NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
                networkUtil.write("Request Other Club Player List");
                networkUtil.write(LogInController.username);
                networkUtil.write(club);
                try {
                    players = (ArrayList<Player>) networkUtil.read();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                networkUtil.closeConnection();
                loadOtherPlayerCells(players);
                startScheduledOtherUpdate();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void UpdateOtherListRecursive() throws ClassNotFoundException {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request Other Club Player List recursive");
            networkUtil.write(LogInController.username);
            String club = otherClub.getValue();
            networkUtil.write(club);
            String response = (String) networkUtil.read();
            if (response.equals("Change")) {
                players = (ArrayList<Player>) networkUtil.read();
                Platform.runLater(() -> loadOtherPlayerCells(players));
            }
            networkUtil.closeConnection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startScheduledOtherUpdate() {
        timer2 = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UpdateOtherListRecursive();
                    updateClubList();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        timer2.scheduleAtFixedRate(task, 0, Server.changeTime);
    }

    public void updateClubList() {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Give me all club name recursive");
            networkUtil.write(LogInController.username);
            String response = (String) networkUtil.read();
            if (response.equals("Change")) {
                @SuppressWarnings("unchecked")
                ArrayList<String> clubNames = (ArrayList<String>) networkUtil.read();
                Platform.runLater(() -> {
                    otherClub.getItems().clear();
                    otherClub.getItems().add("My Club");
                    clubNames.remove(LogInController.username);
                    otherClub.getItems().addAll(clubNames);
                });
            }
            networkUtil.closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOtherPlayerCells(List<Player> filteredPlayers) {
        playerContainer.getChildren().clear(); // Clear existing player cells
        System.out.println("Load player cells");
        for (Player player : filteredPlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlayerCell.fxml"));
                AnchorPane playerCell = loader.load();

                PlayerCellController controller = loader.getController();
                controller.setPlayer(player);
                controller.playerName.setText(player.getName());
                controller.PlayerSalary.setText(String.valueOf(player.getWeeklySalary()));
                controller.height.setText("Height: " + player.getHeight());
                controller.playerPostion.setText(player.getPosition());
                controller.Age.setText("Age: " + player.getAge());
                controller.ClubName.setText(player.getClub());
                controller.CountryName.setText(player.getCountry());
                Long price = (long) (player.getWeeklySalary() * 52);
                controller.price.setText(price + "$");
                if (Server.playerPhoto) {
                    try {
                        String imagepath = "src/photos/country/" + player.getCountry() + ".jpg";
                        Image countryImage = new Image(new FileInputStream(imagepath));
                        controller.CountryPic.setImage(countryImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/country/Country.jpg";
                            Image countryImage = new Image(new FileInputStream(imagepath));
                            controller.CountryPic.setImage(countryImage);
                        } catch (Exception e1) {
                            System.out.println("Error: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    try {
                        String imagepath = "src/photos/club_mini/" + player.getClub() + ".png";
                        Image clubImage = new Image(new FileInputStream(imagepath));
                        controller.ClubPic.setImage(clubImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/club_mini/Club.png";
                            Image clubImage = new Image(new FileInputStream(imagepath));
                            controller.ClubPic.setImage(clubImage);
                        } catch (Exception e1) {
                            System.out.println("Error: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }

                    // Set profile photo
                    try {
                        String imagepath = "src/photos/profile/" + player.getName() + ".png";
                        Image profileImage = new Image(new FileInputStream(imagepath));
                        controller.profilePhoto.setImage(profileImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/profile/profile.png";
                            Image profileImage = new Image(new FileInputStream(imagepath));
                            controller.profilePhoto.setImage(profileImage);
                        } catch (Exception e3) {
                            System.out.println("Error: " + e3.getMessage());
                            e3.printStackTrace();
                        }
                    }
                }
                // Create a circle clip for the profile photo
                double radius = 50; // Adjust the radius as needed
                Circle clip = new Circle(radius, radius, radius);
                controller.profilePhoto.setClip(clip);

                // Ensure the circle is centered in the ImageView
                controller.profilePhoto.setFitWidth(radius * 2);
                controller.profilePhoto.setFitHeight(radius * 2);

                if(player.getState()==true){
                    controller.sellPlayer.setText("In Sell");
                }
                controller.sellPlayer.setDisable(true);
                playerContainer.getChildren().add(playerCell);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();

            }
        }
    }


    @SuppressWarnings("unchecked")
    public void UpdateListRecursive() throws ClassNotFoundException {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request PlayerList Update recursive");
            networkUtil.write(LogInController.username);
            String response = (String) networkUtil.read();
            if (response.equals("Change")) {
                players = (ArrayList<Player>) networkUtil.read();
                Platform.runLater(() -> loadPlayerCells(players));
            }
            // System.out.println("UpdateListRecursive: " + response);
            networkUtil.closeConnection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
    }

    @SuppressWarnings("unchecked")
    public void UpdateList() throws ClassNotFoundException {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request PlayerList Update");
            networkUtil.write(LogInController.username);
            players = (ArrayList<Player>) networkUtil.read();
            networkUtil.closeConnection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startScheduledUpdate() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UpdateListRecursive();
                    updateClubList();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, Server.changeTime);
    }

    void updateSearch(String searchCriteria) {
        try {
            UpdateList();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try{
            if (searchFilter.getValue().equals("Name")) {
                searchCriteria = Utility.capitalize(searchCriteria.trim());
                System.out.println("Search by name");
                players = Player.SearchByName(searchCriteria, players);
            } else if (searchFilter.getValue().equals("Country")) {
                searchCriteria = Utility.capitalize(searchCriteria.trim());
                players = Player.SearchByCountry(searchCriteria, players);
            } else if (searchFilter.getValue().equals("Club")) {
                searchCriteria = Utility.capitalize(searchCriteria.trim());
                players = Player.SearchByClub(searchCriteria, players);
            } else if (searchFilter.getValue().equals("Position")) {
                searchCriteria = Utility.capitalize(searchCriteria.trim());
                players = Player.SearchByPosition(searchCriteria, players);
            } else if (searchFilter.getValue().equals("Weekly Sallary")) {
                try {
                    searchCriteria=Utility.capitalize(searchCriteria.trim());
                    if(searchCriteria.contains("Max")){
                        players=Player.SearchmaxSalary(players);
                    }
                    else if(searchCriteria.contains("Min")){
                        players=Player.SearchminSalary(players);
                    }
                    else if (searchCriteria.contains("to")) {
                        String[] parts = searchCriteria.split("to");
                        Long min = Long.parseLong(parts[0].trim());
                        Long max = Long.parseLong(parts[1].trim());
                        players = Player.SearchByWeeklySalary(min, max, players);
                    } else if (searchCriteria.contains("-")) {
                        String[] parts = searchCriteria.split("-");
                        Long min = Long.parseLong(parts[0].trim());
                        Long max = Long.parseLong(parts[1].trim());
                        players = Player.SearchByWeeklySalary(min, max, players);
                    } else {
                        Long searchCriteriaLong = Long.parseLong(searchCriteria);
                        players = Player.SearchByWeeklySalary(searchCriteriaLong, players);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (searchFilter.getValue().equals("Height")) {
                try {
                    searchCriteria=Utility.capitalize(searchCriteria.trim());
                    if(searchCriteria.contains("Max")){
                        players=Player.SearchmaxHeight(players);
                    }
                    else if(searchCriteria.contains("Min")){
                        players=Player.SearchminHeight(players);
                    }
                    else if (searchCriteria.contains("to")) {
                        String[] parts = searchCriteria.split("to");
                        double min = Double.parseDouble(parts[0].trim());
                        double max = Double.parseDouble(parts[1].trim());
                        players = Player.SearchByHeight(min, max, players);
                    } else if (searchCriteria.contains("-")) {
                        String[] parts = searchCriteria.split("-");
                        double min = Double.parseDouble(parts[0].trim());
                        double max = Double.parseDouble(parts[1].trim());
                        players = Player.SearchByHeight(min, max, players);
                    } else {
                        double searchCriteriaDouble = Double.parseDouble(searchCriteria);
                        players = Player.SearchByHeight(searchCriteriaDouble, players);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (searchFilter.getValue().equals("Age")) {
                try {
                    searchCriteria=Utility.capitalize(searchCriteria.trim());
                    if(searchCriteria.contains("Max")){
                        players=Player.SearchmaxAge(players);
                    }
                    else if(searchCriteria.contains("Min")){
                        players=Player.SearchminAge(players);
                    }
                    else if (searchCriteria.contains("to")) {
                        String[] parts = searchCriteria.split("to");
                        int min = Integer.parseInt(parts[0].trim());
                        int max = Integer.parseInt(parts[1].trim());
                        players = Player.SearchByAge(min, max, players);
                    } else if (searchCriteria.contains("-")) {
                        String[] parts = searchCriteria.split("-");
                        int min = Integer.parseInt(parts[0].trim());
                        int max = Integer.parseInt(parts[1].trim());
                        players = Player.SearchByAge(min, max, players);
                    } else {
                        int searchCriteriaInt = Integer.parseInt(searchCriteria);
                        players = Player.SearchByAge(searchCriteriaInt, players);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    private void handleSearch(ActionEvent event) throws ClassNotFoundException {
        try {
            String searchCriteria = searchFeild.getText();
            // UpdateList();
            if (searchCriteria.isEmpty()) {
                if(isotherClub){
                    loadOtherPlayerCells(players);
                }
                else{
                    UpdateList();
                    loadPlayerCells(players);
                }
                return;
            }
            updateSearch(searchCriteria);
            if(isotherClub){
                loadOtherPlayerCells(players);
            }
            else{
                loadPlayerCells(players);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlayerCells(List<Player> filteredPlayers) {
        playerContainer.getChildren().clear(); // Clear existing player cells
        System.out.println("Load Player Cells");
        for (Player player : filteredPlayers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlayerCell.fxml"));
                AnchorPane playerCell = loader.load();

                PlayerCellController controller = loader.getController();
                controller.setPlayer(player);
                controller.playerName.setText(player.getName());
                controller.PlayerSalary.setText(String.valueOf(player.getWeeklySalary()));
                controller.height.setText("Height: " + player.getHeight());
                controller.playerPostion.setText(player.getPosition());
                controller.Age.setText("Age: " + player.getAge());
                controller.ClubName.setText(player.getClub());
                controller.CountryName.setText(player.getCountry());
                Long price = (long) (player.getWeeklySalary() * 52);
                controller.price.setText(price + "$");
                if (Server.playerPhoto) {
                    try {
                        String imagepath = "src/photos/country/" + player.getCountry() + ".jpg";
                        Image countryImage = new Image(new FileInputStream(imagepath));
                        controller.CountryPic.setImage(countryImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/country/Country.jpg";
                            Image countryImage = new Image(new FileInputStream(imagepath));
                            controller.CountryPic.setImage(countryImage);
                        } catch (Exception e1) {
                            System.out.println("Error: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    try {
                        String imagepath = "src/photos/club_mini/" + player.getClub() + ".png";
                        Image clubImage = new Image(new FileInputStream(imagepath));
                        controller.ClubPic.setImage(clubImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/club_mini/Club.png";
                            Image clubImage = new Image(new FileInputStream(imagepath));
                            controller.ClubPic.setImage(clubImage);
                        } catch (Exception e1) {
                            System.out.println("Error: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }

                    // Set profile photo
                    try {
                        String imagepath = "src/photos/profile/" + player.getName() + ".png";
                        Image profileImage = new Image(new FileInputStream(imagepath));
                        controller.profilePhoto.setImage(profileImage);
                    } catch (Exception e) {
                        try {
                            String imagepath = "src/photos/profile/profile.png";
                            Image profileImage = new Image(new FileInputStream(imagepath));
                            controller.profilePhoto.setImage(profileImage);
                        } catch (Exception e3) {
                            System.out.println("Error: " + e3.getMessage());
                            e3.printStackTrace();
                        }
                    }
                }
                double radius = 50; // Adjust the radius as needed
                Circle clip = new Circle(radius, radius, radius);
                controller.profilePhoto.setClip(clip);

                // Ensure the circle is centered in the ImageView
                controller.profilePhoto.setFitWidth(radius * 2);
                controller.profilePhoto.setFitHeight(radius * 2);

                if(player.getState()==true){
                    controller.sellPlayer.setText("In Sell");
                    controller.sellPlayer.setDisable(true);
                }
                if (LogInController.username.equals("admin")) {
                    controller.sellPlayer.setDisable(true);
                }
                playerContainer.getChildren().add(playerCell);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();

            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (LogInController.username.equals("admin") == false) {
            clubField.setVisible(false);
        }
        clubName.setText(LogInController.username);
        searchFilter.getItems().addAll("Name", "Country", "Club", "Position", "Weekly Sallary", "Height",
                "Age");
        searchFilter.setValue("Name");
        logout.getItems().addAll("Logout","change Password");
        otherClub.getItems().add("My Club");

        try{
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Give me all club name");
            @SuppressWarnings("unchecked")
            ArrayList<String> clubNames = (ArrayList<String>) networkUtil.read();
            networkUtil.closeConnection();
            clubNames.remove(LogInController.username);
            otherClub.getItems().addAll(clubNames);
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            UpdateList();
            loadPlayerCells(players);
            startScheduledUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButtonAction() {
        String name = nameField.getText();
        String country = countryField.getText();
        String heightText = heightField.getText();
        String ageText = ageField.getText();
        String club = clubField.getText();
        String position = positionField.getText();
        String numberText = numberField.getText();
        String salaryText = salaryField.getText();

        if (name.isEmpty() || country.isEmpty() || heightText.isEmpty() || ageText.isEmpty() || position.isEmpty()
                || numberText.isEmpty() || salaryText.isEmpty()) {
            if (LogInController.username.equals("admin") && club.isEmpty()) {
                Utility.showAlert("Error", "All fields must be filled!", AlertType.ERROR);
                return;
            }
            Utility.showAlert("Error", "All fields must be filled!", AlertType.ERROR);
            return;
        }
        if (LogInController.username.equals("admin") == false) {
            club = LogInController.username;
        }

        int age;
        double height;
        int number;
        int salary;

        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            Utility.showAlert("Error", "Invalid age value!", AlertType.ERROR);
            return;
        }

        try {
            height = Double.parseDouble(heightText);
        } catch (NumberFormatException e) {
            Utility.showAlert("Error", "Invalid height value!", AlertType.ERROR);
            return;
        }

        try {
            number = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            Utility.showAlert("Error", "Invalid number value!", AlertType.ERROR);
            return;
        }

        try {
            salary = Integer.parseInt(salaryText);
        } catch (NumberFormatException e) {
            Utility.showAlert("Error", "Invalid salary value!", AlertType.ERROR);
            return;
        }

        Player newPlayer = new Player(name, country, age, height, club, position, number, salary);
        Server.playerList.add(newPlayer);

        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Client Want to update PlayerList");
            networkUtil.write(newPlayer);
            Utility.showAlert("Success", "Player added successfully!", AlertType.INFORMATION);
            clearFields();
            networkUtil.closeConnection();
            // try {
            //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPlayer.fxml"));
            //     Parent searchPlayerRoot = loader.load();
            //     Stage stage = (Stage) addButton.getScene().getWindow();
            //     stage.setScene(new Scene(searchPlayerRoot));
            //     stage.show();
            // } catch (IOException e) {
            //     // System.out.println("Error: " + e.getMessage());
            //     e.printStackTrace();
            // }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Utility.showAlert("Error", "Could not connect to server", AlertType.ERROR);
        }
    }

    private void clearFields() {
        nameField.clear();
        countryField.clear();
        heightField.clear();
        ageField.clear();
        clubField.clear();
        positionField.clear();
        salaryField.clear();
        numberField.clear();
    }

    @FXML
    private void handleBackButtonAction() throws IOException {
        if (timer != null) {
            timer.cancel();
        }

        if(logout.getValue().equals("change Password")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChangePassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else{
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("I want to logout");
            networkUtil.write(LogInController.username);
            networkUtil.closeConnection();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogIn.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }

        // NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
        // networkUtil.write("I want to logout");
        // networkUtil.write(LogInController.username);
        // networkUtil.closeConnection();
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LogIn.fxml"));
        // Parent root = loader.load();

        // Stage stage = (Stage) logout.getScene().getWindow();
        // stage.setScene(new Scene(root));
        // stage.show();
    }

    @FXML
    private void handleAddPlayerButton(ActionEvent event) {
        try {
            if (timer != null) {
                timer.cancel();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddPlayer.fxml"));
            Parent addPlayerRoot = loader.load();
            Stage stage = (Stage) addPlayerButton.getScene().getWindow();
            stage.setScene(new Scene(addPlayerRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPlayerButton(ActionEvent event) {
        try {
            if (timer != null) {
                timer.cancel();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewPlayer.fxml"));
            Parent viewPlayerRoot = loader.load();
            Stage stage = (Stage) viewPlayerButton.getScene().getWindow();
            stage.setScene(new Scene(viewPlayerRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchPlayerButton(ActionEvent event) throws ClassNotFoundException {
        try {
            if (timer != null) {
                timer.cancel();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchPlayer.fxml"));
            Parent searchPlayerRoot = loader.load();
            Stage stage = (Stage) searchPlayerButton.getScene().getWindow();
            stage.setScene(new Scene(searchPlayerRoot));
            stage.show();
        } catch (IOException e) {
            // System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
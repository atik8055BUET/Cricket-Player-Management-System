package Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import Source.NetworkUtil;
import Source.Player;
import Source.Server;
import Source.Utility;

public class changePasswordController implements Initializable {
    @FXML
    private Button Search;

    @FXML
    private Label clubBalance;

    @FXML
    private Rectangle clubColour;

    @FXML
    private Label clubName;

    @FXML
    private ComboBox<String> logout;

    @FXML
    private VBox playerContainer;

    @FXML
    private TextField searchFeild;

    @FXML
    private ComboBox<String> searchFilter;

    public ArrayList<Player> players = new ArrayList<Player>();

    @FXML
    private Button addPlayerButton;

    @FXML
    private Button searchPlayerButton;

    @FXML
    private Button viewPlayerButton;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField oldPassword;

    @FXML
    private PasswordField renewPassword;

    @FXML
    private Button UpdatePassword;

    private Timer timer;

    private Timer timer2 = null;

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
            if (timer2 != null) {
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
        }
    }

    private void startScheduledOtherUpdate() {
        timer2 = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UpdateOtherListRecursive();
                    updateBalance();
                    updateClubList();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        timer2.scheduleAtFixedRate(task, 0, Server.changeTime);
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

                if (player.getState() == true) {
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
    private void handleupdatepassword(ActionEvent event) {
        try {
            if (timer != null) {
                timer.cancel();
            }
            String oldPass = oldPassword.getText();
            String newPass = newPassword.getText();
            String renewPass = renewPassword.getText();
            if (oldPass.isEmpty() || newPass.isEmpty() || renewPass.isEmpty()) {
                Utility.showAlert("Error", "Please fill all the fields", Alert.AlertType.ERROR);
                return;
            }
            if (!newPass.equals(renewPass)) {
                Utility.showAlert("Error", "New Password and Renew Password does not match", Alert.AlertType.ERROR);
                return;
            }
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Change Password");
            networkUtil.write(LogInController.username);
            networkUtil.write(oldPass);
            networkUtil.write(newPass);
            String response = (String) networkUtil.read();
            if (response.equals("Password Changed")) {
                Utility.showAlert("Success", "Password Changed Successfully", Alert.AlertType.INFORMATION);
            } else {
                Utility.showAlert("Error", "Old Password is incorrect", Alert.AlertType.ERROR);
            }
            networkUtil.closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        oldPassword.clear();
        newPassword.clear();
        renewPassword.clear();
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

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        if (timer != null) {
            timer.cancel();
        }

        if (logout.getValue().equals("change Password")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChangePassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
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
            networkUtil.closeConnection();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateBalance() {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request Club Balance");
            networkUtil.write(LogInController.username);
            Long clubcurrBalance = (Long) networkUtil.read();
            Platform.runLater(() -> clubBalance.setText("Balance: " + clubcurrBalance + "$"));
            networkUtil.closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void UpdateList() throws ClassNotFoundException {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request PlayerList Update");
            networkUtil.write(LogInController.username);
            players = (ArrayList<Player>) networkUtil.read();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
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
        }
    }

    private void startScheduledUpdate() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    UpdateListRecursive();
                    updateBalance();
                    updateClubList();
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Schedule the task to run every 3 seconds (3000ms) with an initial delay of 0
        timer.scheduleAtFixedRate(task, 0, Server.changeTime);
    }

    void updateSearch(String searchCriteria) {
        try {
            UpdateList();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
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
                    searchCriteria = Utility.capitalize(searchCriteria.trim());
                    if (searchCriteria.contains("Max")) {
                        players = Player.SearchmaxSalary(players);
                    } else if (searchCriteria.contains("Min")) {
                        players = Player.SearchminSalary(players);
                    } else if (searchCriteria.contains("to")) {
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
                    searchCriteria = Utility.capitalize(searchCriteria.trim());
                    if (searchCriteria.contains("Max")) {
                        players = Player.SearchmaxHeight(players);
                    } else if (searchCriteria.contains("Min")) {
                        players = Player.SearchminHeight(players);
                    } else if (searchCriteria.contains("to")) {
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
                    searchCriteria = Utility.capitalize(searchCriteria.trim());
                    if (searchCriteria.contains("Max")) {
                        players = Player.SearchmaxAge(players);
                    } else if (searchCriteria.contains("Min")) {
                        players = Player.SearchminAge(players);
                    } else if (searchCriteria.contains("to")) {
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

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    @FXML
    private void handleSearch(ActionEvent event) throws ClassNotFoundException {
        try {
            String searchCriteria = searchFeild.getText();
            // UpdateList();
            if (searchCriteria.isEmpty()) {
                if (isotherClub) {
                    loadOtherPlayerCells(players);
                } else {
                    loadPlayerCells(players);
                }
                return;
            }
            updateSearch(searchCriteria);
            if (isotherClub) {
                loadOtherPlayerCells(players);
            } else {
                UpdateList();
                loadPlayerCells(players);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPlayerCells(List<Player> filteredPlayers) {
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

                if (LogInController.username.equals("admin")) {
                    controller.sellPlayer.setDisable(true);
                }
                if (player.getState() == true) {
                    controller.sellPlayer.setText("In Sell");
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
        searchFilter.getItems().addAll("Name", "Country", "Club", "Position", "Weekly Sallary", "Height", "Age");
        searchFilter.setValue("Name");
        logout.getItems().addAll("Logout", "change Password");
        otherClub.getItems().add("My Club");
        clubName.setText(LogInController.username);

        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Give me all club name");
            @SuppressWarnings("unchecked")
            ArrayList<String> clubNames = (ArrayList<String>) networkUtil.read();
            networkUtil.closeConnection();
            clubNames.remove(LogInController.username);
            otherClub.getItems().addAll(clubNames);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Request Club Balance");
            networkUtil.write(LogInController.username);
            Long clubcurrBalance = (Long) networkUtil.read();
            clubBalance.setText("Balance: " + clubcurrBalance + "$");
            networkUtil.closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        // try {
        // String imagepath = "src/photos/club/" + LogInController.username + ".png";
        // Image clubImage = new Image(new FileInputStream(imagepath));
        // clubIcon.setImage(clubImage);
        // } catch (Exception e) {
        // System.out.println("Error: " + e.getMessage());
        // }
        try {
            UpdateList();
            loadPlayerCells(players);
            startScheduledUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
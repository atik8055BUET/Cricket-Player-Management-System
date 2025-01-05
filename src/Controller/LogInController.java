package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;

import Source.NetworkUtil;

public class LogInController {

    @FXML
    private ComboBox<String> usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorMessage;

    @FXML
    private Button loginButton;

    public static String username;
    public static String password;

    @FXML
    private void handleLogin(ActionEvent event) {
        // String username = usernameField.getText();
        String username=usernameField.getValue();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please enter username and password!");
            return;
        }
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("I want to login");
            networkUtil.write(username);
            networkUtil.write(password);
            String response = (String) networkUtil.read();
            if (response.equals("Login successful")) {
                LogInController.username = username;
                LogInController.password = password;
                loadDashboard();
            } else if (response.equals("Already Logged In")) {
                errorMessage.setText(username + " is already logged in!");
            } else {
                errorMessage.setText("Invalid username or password!");
            }
            networkUtil.closeConnection();
        } catch (Exception e) {
            errorMessage.setText("Invalid username or password!");
        }
    }

    // public void updateClubList() {
    //     try {
    //         if(LogInController.username.equals("admin")){
    //             return;
    //         }
    //         NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
    //         networkUtil.write("Give me all club name recursive");
    //         String response = (String) networkUtil.read();
    //         if (response.equals("No Change")) {
    //             return;
    //         }
    //         @SuppressWarnings("unchecked")
    //         ArrayList<String> clubNames = (ArrayList<String>) networkUtil.read();
    //         Platform.runLater(() -> {
    //             usernameField.getItems().clear();
    //             for (String clubName : clubNames) {
    //                 usernameField.getItems().add(clubName);
    //             }
    //         });
    //         networkUtil.closeConnection();
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //     }
    // }

    @SuppressWarnings("unchecked")
    @FXML
    private void initialize() {
        try {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("Give me all club name");
            ArrayList<String> usernames = (ArrayList<String>) networkUtil.read();
            for (String username : usernames) {
                usernameField.getItems().add(username);
            }
            networkUtil.closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            errorMessage.setText("Server is not running !!!");
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchPlayer.fxml"));
            Parent searchPlayerRoot = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(searchPlayerRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package Controller;

import java.io.IOException;

import Source.NetworkUtil;
import Source.Player;
import Source.Utility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class PlayerBuyController {

    @FXML
    public Text Age;

    @FXML
    public Text ClubName;

    @FXML
    public ImageView ClubPic;

    @FXML
    public Text CountryName;

    @FXML
    public ImageView CountryPic;

    @FXML
    public Text PlayerSalary;

    @FXML
    public Text height;

    @FXML
    public Text playerName;

    @FXML
    public Text playerPostion;

    @FXML
    public ImageView profilePhoto;

    @FXML
    public Button buyPlayer;

    @FXML
    public Text price;

    public Player player = new Player();

    public void setPlayer(Player player) {
        this.player = player;
    }

    @FXML
    void handleBuyPlayer(ActionEvent event) throws IOException {
        buyPlayer.setDisable(true);
        if (LogInController.username.equals("admin")) {
            Utility.showAlert("Error", "Admin can't buy player", AlertType.ERROR);
            return;
        }

        if (this.player != null) {
            NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
            networkUtil.write("I want to buy");
            networkUtil.write(LogInController.username);
            networkUtil.write(this.player.getName());
            try {
                String response = (String) networkUtil.read();
                if (response.equals("Insufficient Balance")) {
                    Utility.showAlert("Error", "Insufficient Balance", AlertType.ERROR);
                }
                else if (response.equals("Not Available")) {
                    Utility.showAlert("Error", "Player Not Available", AlertType.ERROR);
                } else {
                    Utility.showAlert("Success", "Player Bought", AlertType.INFORMATION);
                }
                System.out.println("Player Bought");
            } catch (Exception e) {
                // System.out.println("Error: " + e.getMessage());
                System.out.println("Error player buy");
                e.printStackTrace();
            } finally {
                networkUtil.closeConnection();

                // try {
                //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewPlayer.fxml"));
                //     Parent searchPlayerRoot = loader.load();
                //     Stage stage = (Stage) buyPlayer.getScene().getWindow();
                //     stage.setScene(new Scene(searchPlayerRoot));
                //     stage.show();
                // } catch (IOException e) {
                //     // System.out.println("Error: " + e.getMessage());
                //     e.printStackTrace();
                // }
            }

        }


    }

}

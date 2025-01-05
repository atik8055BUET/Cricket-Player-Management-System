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

public class PlayerCellController {
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
    public Button sellPlayer;

    @FXML
    public Text price;

    public Player player = null;

    @FXML
    public void handleSellPlayer(ActionEvent event) throws IOException {
        if (LogInController.username.equals("admin")) {
            Utility.showAlert("Error", "Admin can't Sell player", AlertType.ERROR);
            return;
        }
        NetworkUtil networkUtil = new NetworkUtil("localhost", 12345);
        networkUtil.write("I want to sell");
        networkUtil.write(player.getName());
        sellPlayer.setDisable(true);
        sellPlayer.setText("In Sell");
        networkUtil.closeConnection();
        Utility.showAlert("Success", "Player is in sell", AlertType.INFORMATION);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}

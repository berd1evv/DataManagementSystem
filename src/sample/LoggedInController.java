package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.net.URL;
import java.util.ResourceBundle;

public class LoggedInController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Button userSend;
    @FXML
    private TextArea content;

    public void getData (ActionEvent actionEvent){
        System.out.println(content.getText());
        DBUtils.writeToDatabase(content.getText());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"sample.fxml", "log in", null);

            }
        });

    }
    public void setUserInformation(String username){
        label_welcome.setText(("Welcome " + username + "!"));

    }
}

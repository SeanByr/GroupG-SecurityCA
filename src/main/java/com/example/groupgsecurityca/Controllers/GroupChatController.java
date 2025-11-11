package com.example.groupgsecurityca.Controllers;

import com.example.groupgsecurityca.Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import lombok.Setter;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupChatController implements Initializable {

    @FXML
    private Button sendMsgBTN;

    @FXML
    private TextField sendMsgTF;

    @FXML
    private Label clientNameLB;

    @FXML
    private VBox clientVBox;

    @FXML
    private ScrollPane clientGroupChatSP;

    @Setter
    private Client client;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    // method for displaying the message this.client sent to the group chat
    public void StartChat(String username){
        clientNameLB.setText(username);

        clientVBox.heightProperty().addListener((observable, oldValue, newValue) ->
            clientGroupChatSP.setVvalue((Double) newValue));

        client.ListenForMessages(clientVBox);



        sendMsgBTN.setOnAction(event -> {
            String message = sendMsgTF.getText();
            if(!message.isEmpty()){
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));

                Text text = new Text(message);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-color: rgb(239, 242, 255); " +
                        "-fx-background-color: rgb(15, 125, 242); " +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.945, 0.966));

                hBox.getChildren().add(textFlow);
                clientVBox.getChildren().add(hBox);

                client.SendMessage(message);
                sendMsgTF.clear();

            }
        });
    }

    public static void addLabel(String receivedMessage, VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(receivedMessage);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}

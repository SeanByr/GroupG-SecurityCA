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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/*
Controller class for the GroupChat UI
manages the user interface for group chat portion of the application
handles the sending and displaying of messages from each client, integrated with the client class
 */

public class GroupChatController implements Initializable {

    //fxml UI components used within the group chat GUI
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
    public void initialize(URL url, ResourceBundle resourceBundle) {}


    /*
    starts the chat session for the give client.
    listens for incoming messages via each client
    handles message submission
    when the send button is pressed, it displays the message locally and sends it securely to all clients current logged in
     */
    public void StartChat(String username){
        //sets the clients username as the clientName label on the UI
        clientNameLB.setText(username);

        //function for ensuring auto scrolling when new messages are sent.
        clientVBox.heightProperty().addListener((observable, oldValue, newValue) ->
            clientGroupChatSP.setVvalue((Double) newValue));

        //start a background thread that listens for incoming messages and updates the UI
        client.ListenForMessages(clientVBox);


        // sets up the send button functionality
        sendMsgBTN.setOnAction(event -> {
            //gets teh text in the text field
            String message = sendMsgTF.getText();
            if(!message.isEmpty()){
                //if its not empty, align the hBox within the VBox to the right.
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                //style the message label using textflow
                Text text = new Text(message);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-color: rgb(239, 242, 255); " +
                        "-fx-background-color: rgb(15, 125, 242); " +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.945, 0.966));

                //add the styled label to the hBox
                //add the hBox with the styled label onto the VBox to be displayed to the client
                hBox.getChildren().add(textFlow);
                clientVBox.getChildren().add(hBox);

                //send the message securely via the client encryption the message with AES and RSA
                client.SendMessage(message);
                sendMsgTF.clear();

            }
        });
    }

    // method for showing other client messages from the group chat in the gui
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

        //used platform.runLater to update teh UI on the JavaFX application thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}

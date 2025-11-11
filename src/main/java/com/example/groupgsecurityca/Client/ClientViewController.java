package com.example.groupgsecurityca.Client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientViewController implements Initializable {

    @FXML
    private Button button_send;

    @FXML
    private TextField textField_Message;

    @FXML
    private Label clientName;

    @FXML
    private VBox vBox_Message;

    @FXML
    private ScrollPane scrollPane_Main;

    @FXML
    private AnchorPane anchorPane_Main;

    @Setter
    private Client client;

//    public void initializeChat(String username) {
//        clientName.setText(username);
//        ClientMessageHandler();
//    }

    public void initializeChat(String username) {
        clientName.setText(username);

        // Scroll auto-scroll setup
        vBox_Message.heightProperty().addListener((obs, oldVal, newVal) ->
                scrollPane_Main.setVvalue((Double) newVal));

        // Start listening for messages
        client.ListenForMessages(vBox_Message);

        // Set up send button
        button_send.setOnAction(event -> {
            String message = textField_Message.getText();
            if (!message.isEmpty()) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));

                Text text = new Text(message);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-color: rgb(239,242,255); " +
                        "-fx-background-color: rgb(15,125,242);" +
                        "-fx-background-radius: 20px;");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.945, 0.966));

                hBox.getChildren().add(textFlow);
                vBox_Message.getChildren().add(hBox);

                //client.sendMessage(message);
                textField_Message.clear();
            }
        });
    }

    public static void addLabel(String recievedMessage, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(recievedMessage);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);" + "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        vBox_Message.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                scrollPane_Main.setVvalue((Double) newValue);
//            }
//        });
//
//        client.ListenForMessages(vBox_Message);
//
//        button_send.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                String message = textField_Message.getText();
//                if(!message.isEmpty()){
//                    HBox hBox = new HBox();
//                    hBox.setAlignment(Pos.CENTER_RIGHT);
//
//                    hBox.setPadding(new Insets(5, 5, 5, 10));
//                    Text text = new Text(message);
//                    TextFlow textFlow = new TextFlow(text);
//                    textFlow.setStyle("-fx-color: rgb(239,242,255); " +
//                            "-fx-background-color: rgb(15,125,242);" +
//                            " -fx-background-radius: 20px");
//
//                    textFlow.setPadding(new Insets(5, 10, 5, 10));
//                    text.setFill(Color.color(0.934, 0.945, 0.966));
//
//                    hBox.getChildren().add(textFlow);
//                    vBox_Message.getChildren().add(hBox);
//
//
//                    client.sendMessage(message);
//                    textField_Message.clear();
//                }
//
//            }
//        });
//
    }
}

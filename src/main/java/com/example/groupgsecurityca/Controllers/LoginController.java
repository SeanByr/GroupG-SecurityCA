package com.example.groupgsecurityca.Controllers;

import com.example.groupgsecurityca.Client.Client;
import com.example.groupgsecurityca.Security.Login;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button loginBTN;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField passwordTF;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = usernameTF.getText();
                String password = passwordTF.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    LoginErrors("Username and password cant be empty");
                    return;
                }

                /*  Joshua Boyne (23343338)
                    Validates the entered username and password and compares it to the users.txt file information
                    Calls Login.validateUser() to check if the information matches
                */
                if (!Login.validateUser(username, password)) {
                    LoginErrors("Invalid username or password.");
                    return;
                }

                try {
                    Socket socket = new Socket("localhost", 1234);
                    Client client = new Client(socket, username, password);

                    FXMLLoader groupChatLoader = new FXMLLoader(getClass().getResource("/com/example/groupgsecurityca/client-groupchat-view.fxml"));
                    Parent root = groupChatLoader.load();

                    GroupChatController groupChatController = groupChatLoader.getController();
                    groupChatController.setClient(client);
                    groupChatController.StartChat(username);

                    Stage stage = (Stage) loginBTN.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Client: " + username);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TODO : show errors upon entering wrong credentials into fields

    /*
        Joshua Boyne (23343338)
        Displays login errors (invalid information, empty fields etc)
     */

    public void LoginErrors(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

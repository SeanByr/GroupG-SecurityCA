package com.example.groupgsecurityca.Controllers;

import com.example.groupgsecurityca.Client.Client;
import com.example.groupgsecurityca.Security.HashingManager;
import com.example.groupgsecurityca.Security.Login;
import com.example.groupgsecurityca.Security.SaltGenerator;
import com.example.groupgsecurityca.Security.UserRecord;
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
import javafx.scene.control.*;
import java.time.LocalDate;


public class LoginController implements Initializable {
    @FXML
    private Button loginBTN;

    @FXML
    private TextField usernameTF;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private Button createAccountBTN;

    @FXML
    private Label registerLabel;

    @FXML
    private TextField regUsernameTF;

    @FXML
    private PasswordField regPasswordTF;

    @FXML
    private PasswordField regConfirmPasswordTF;

    @FXML
    private Button registerBTN;

    @FXML
    private Button backToLoginBTN;


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

    /*
        Joshua Boyne (23343338)
        This method hides the login fields and shows the registration fields when
        the create account button is selected
     */
    @FXML
    private void showRegistration() {
        //hide login
        usernameTF.setVisible(false);
        passwordTF.setVisible(false);
        loginBTN.setVisible(false);
        createAccountBTN.setVisible(false);

        //show registration
        registerLabel.setVisible(true);
        regUsernameTF.setVisible(true);
        regPasswordTF.setVisible(true);
        regConfirmPasswordTF.setVisible(true);
        registerBTN.setVisible(true);
        backToLoginBTN.setVisible(true);
    }

    /*
        Joshua Boyne (23343338)
        Hides the registration fields and shows the login fields when the
        back to login button is pressed or when a successful registration happens
     */
    @FXML
    private void showLogin() {
        //hide registration
        regUsernameTF.clear();
        regPasswordTF.clear();
        regConfirmPasswordTF.clear();
        registerLabel.setVisible(false);
        regUsernameTF.setVisible(false);
        regPasswordTF.setVisible(false);
        regConfirmPasswordTF.setVisible(false);
        registerBTN.setVisible(false);
        backToLoginBTN.setVisible(false);

        //show login
        usernameTF.setVisible(true);
        passwordTF.setVisible(true);
        loginBTN.setVisible(true);
        createAccountBTN.setVisible(true);
    }

    /*
        Joshua Boyne (23343338)
        Handles the account creation, it validates inputs, checks for duplicates, generates salts/hash,
        saves to users.txt and switches back to login on success
     */

    @FXML
    private void registerAccount() {
        String username = regUsernameTF.getText().trim();
        String password = regPasswordTF.getText();
        String confirmPassword = regConfirmPasswordTF.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            LoginErrors("All fields are required.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            LoginErrors("Passwords do not match.");
            return;
        }
        if (Login.validateUser(username, "random")) { //checks if username already exists
            LoginErrors("Username already exists. Choose a different one.");
            return;
        }

        try {
            String salt = SaltGenerator.generateSalt();
            String hash = HashingManager.hashKey(password, salt);
            LocalDate createdDate = LocalDate.now();


            UserRecord newUser = new UserRecord(username, salt.getBytes(), hash.getBytes(),
                    HashingManager.ITERATIONS, HashingManager.ALGORITHM, createdDate);
            if (Login.saveUserToFile(newUser)) {
                // Success: Show message and switch to login
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Registration Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Account created! You can now log in.");
                successAlert.showAndWait();
                showLogin();
            } else {
                LoginErrors("Failed to save account. Try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoginErrors("Registration failed due to an error.");
        }
    }
}
package com.example.groupgsecurityca.Client;

import com.example.groupgsecurityca.Security.Login;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import lombok.Setter;
import com.example.groupgsecurityca.AES.AES_KEY;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientController implements Initializable {

    //client-login
    @FXML
    private Button loginBTN;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField passwordTF;

    @FXML
    private AnchorPane loginAP;

    @Setter
    private Client client;



    @FXML
    private void handleLogin() throws IOException {
        String username = usernameTF.getText();
        String password = passwordTF.getText();

        //check empty fields
        if (username.isEmpty() || password.isEmpty()) {
//            showError();
            System.out.println("Please enter your username and password");
            return;
        }

        //validate user information from user.txt
        if(!Login.validateUser(username, password)){
            System.out.println("Invalid username or password");
            return;
        }

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username, password);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/groupgsecurityca/client-groupchat-view.fxml"));
        Parent root = loader.load();

        ClientViewController controller = loader.getController();
        controller.setClient(client);
//        controller.initializeChat(username);

        Stage stage = (Stage) loginBTN.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

        System.out.println("Successfull login from: "+username);
    }


    //change from login to clienview
//    @FXML
//    private void openClientView(String username) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/clientserverjavafx/client-view.fxml"));
//        Parent root = loader.load();
//
//        Stage stage = (Stage) loginBTN.getScene().getWindow();
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle(username);
//        stage.show();
//    }






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


//            Scanner scan = new Scanner(System.in);
//            System.out.println("Enter name to join the group chat!");
//            String username = scan.nextLine();
//            clientName.setText(username);
//            System.out.println("Enter password:");
//            String password = scan.nextLine();
//            loginBTN.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    Label errorUsernameLabel;
//                    Label errorPasswordLabel;
//
//                    String username = usernameTF.getText();
//                    String password = passwordTF.getText();
//                    if(username.isEmpty() && password.isEmpty()){
//                        errorUsernameLabel = new Label("Please enter your Username");
//                        errorUsernameLabel.setStyle("-fx-text-fill: red");
//                        errorUsernameLabel.setLayoutX(30);
//                        errorUsernameLabel.setLayoutY(141);
//                        loginAP.getChildren().add(errorUsernameLabel);
//
//                        errorPasswordLabel = new Label("Please enter your password");
//                        errorPasswordLabel.setStyle("-fx-text-fill: red");
//                        errorPasswordLabel.setLayoutX(30);
//                        errorPasswordLabel.setLayoutY(221);
//                        loginAP.getChildren().add(errorPasswordLabel);
//                    }
//                    try {
//                        Socket socket = new Socket("localhost", 1234);
//                        client = new Client(socket, username, password);
//                        openClientView();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        loginBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String username = usernameTF.getText();
                String password = passwordTF.getText();

                if (username.isEmpty() || password.isEmpty()) {
//            showError();
                    return;
                }
                try {
                    Socket socket = new Socket("localhost", 1234);
                    Client client = new Client(socket, username, password);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/clientserverjavafx/client-view.fxml"));
                    Parent root = loader.load();

                    ClientViewController controller = loader.getController();
                    controller.setClient(client);
                    controller.initializeChat(username);

                    Stage stage = (Stage) loginBTN.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}

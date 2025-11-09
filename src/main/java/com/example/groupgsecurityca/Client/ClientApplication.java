package com.example.groupgsecurityca.Client;

//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class ClientApplication extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/com/example/groupgsecurityca/client-login-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Client Login");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args){
//
//        launch(args);
//    }
//}

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.groupgsecurityca.Server.ClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //uncomment this when working on a login system
        FXMLLoader clientLoginLoader = new FXMLLoader(ClientApplication.class.getResource("/com/example/groupgsecurityca/client-login-view.fxml"));
        Scene loginScene = new Scene(clientLoginLoader.load(), 478, 396);
        stage.setTitle("Client Login");
        stage.setScene(loginScene);
        stage.show();

//        FXMLLoader clientViewLoader = new FXMLLoader(ClientApplication.class.getResource("/org/example/clientserverjavafx/client-view.fxml"));
//        Scene viewScene = new Scene(clientViewLoader.load(), 478, 396);
//        stage.setTitle("Client");
//        stage.setScene(viewScene);
//        stage.show();
    }

    public static void main(String[] args) throws IOException {

//        Scanner scan = new Scanner(System.in);
//        System.out.println("Enter name to join the group chat!");
//        String username = scan.nextLine();
//        System.out.println("Enter password:");
//        String password = scan.nextLine();
//        Socket socket = new Socket("localhost", 1234);
//        Client client = new Client(socket, username);

        launch();
//        client.ListenForMessages();
//        client.sendMessage();

//        ClientHandler c = new ClientHandler(username, password);
//        System.out.println(c.getClientUsername() + " " + c.getClientPassword());
    }
}

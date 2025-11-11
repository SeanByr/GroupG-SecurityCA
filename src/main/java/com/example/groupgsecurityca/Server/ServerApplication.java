package com.example.groupgsecurityca.Server;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerApplication extends Application {




    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("/com/example/groupgsecurityca/client-groupchat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 478, 396);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
//@Override
//public void start(Stage stage) {
//    stage.setTitle("Server Running");
//    stage.show();
//}

    public static void main(String[] args) throws IOException {
        System.out.println("Opening Port...");
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
//        server.StartServer();
        new Thread(() -> server.StartServer()).start();
        launch();
    }
}
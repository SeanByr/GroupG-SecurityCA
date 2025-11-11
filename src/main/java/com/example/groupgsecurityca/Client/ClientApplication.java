package com.example.groupgsecurityca.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //TODO : replace with either create Client loader or Client Login
        FXMLLoader GroupChatLoader = new FXMLLoader(ClientApplication.class.getResource("/com/example/groupgsecurityca/client-groupchat-view.fxml"));
        Scene scene = new Scene(GroupChatLoader.load(), 478, 396);
        stage.setTitle("Group Chat");
        stage.setScene(scene);
        stage.show();
    }

//    FXMLLoader ClientLoginLoader = new FXMLLoader(ClientApplication.class.getResource("/com/example/groupgsecurityca/client-login-view.fxml"));
//    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//    stage.setTitle("Client Login");
//    stage.setScene(scene);
//    stage.show();

    public static void main(String[] args){

        launch(args);
    }
}

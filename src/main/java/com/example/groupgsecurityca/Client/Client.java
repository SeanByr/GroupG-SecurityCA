package com.example.groupgsecurityca.Client;

import com.example.groupgsecurityca.Controllers.GroupChatController;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import com.example.groupgsecurityca.AES.AES_KEY;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/*
Client Class - Sean Byrne 23343362
Class that represents each Client connected to the server
Class Used for sending and listening for messages from the group chat.


 */

//TODO : RSA encryption / create public and private keys for the clients

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;
    private String password;

    // create client objects that represent each client on the server
    public Client(Socket socket, String username, String password) {
        try {
            this.socket = socket;
            this.username = username;
            this.password = password;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(username);
            out.newLine();
            out.flush();

        }catch(IOException e){
            CloseEverything(socket, in, out);
        }
    }

    // gets the message from the client-groupchat-view.fxml textfield
    // sends the message to the Server(ClientHandler class) to be broadcast to all clients
    // TODO : encrypt the message

    // ****** UNDER NO CIRCUMSTANCES UNCOMMENT THIS METHOD ******
//    public void SendMessage(String sendMessage){
//        try {
//            while(socket.isConnected()) {
//                // TODO : encrypt before message is written out to the server for broadcasting
//
//                out.write(sendMessage);
//                out.newLine();
//                out.flush();
//            }
//        }catch(IOException e){
//            CloseEverything(socket, in, out);
//        }
//    }

    public void SendMessage(String sendMessage){
        try {
            out.write(sendMessage);
            out.newLine();
            out.flush();
        } catch(IOException e){
            CloseEverything(socket, in, out);
        }
    }

    // separate thread that listens for messages broadcast by the Server(ClientHandler class)
    // TODO : decrypt the message
    public void ListenForMessages(VBox vBox){
        new Thread(()->{
            try{
                String receivedMessage;
                while((receivedMessage = in.readLine()) != null) {
                    // TODO : probably have the message decrypted here
                    String decryptedMessage = receivedMessage;
                    Platform.runLater(()->{
                        GroupChatController.addLabel(decryptedMessage, vBox);
                    });
                }
            }catch(IOException e){
                CloseEverything(socket, in, out);
            }
        }).start();
    }


    public void CloseEverything(Socket socket, BufferedReader in, BufferedWriter out){
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}

//public class Client {
//
//    private Socket socket;
//    private BufferedReader in;
//    private BufferedWriter out;
//    private String username;
//    private String password;
//
//    public Client(Socket socket, String username, String password) {
//        try {
//            this.socket = socket;
//            this.username = username;
//            this.password = password;
//            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//
//            out.write(username);
//            out.newLine();
//            out.flush();
//            out.write(password);
//            out.newLine();
//            out.flush();
//        } catch (IOException e) {
//            CloseEverything(socket, in, out);
//        }
//    }
//
//    public void sendMessage(String message) {
//        try{
////            out.write(username);
////            out.newLine();
////            out.flush();
////            out.write(password);
////            out.newLine();
////            out.flush();
//            out.write(message);
//            out.newLine();
//            out.flush();
//        }catch(Exception e){
//            CloseEverything(socket, in, out);
//        }
//    }
//
//    public void ListenForMessages(VBox vbox) {
//        new Thread(() -> {
//            try {
//                String msg;
//                while ((msg = in.readLine()) != null) {
//                    String message = msg;
//                    Platform.runLater(() -> {
//                        ClientViewController.addLabel(message, vbox);
//                    });
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//
//    public void CloseEverything(Socket socket, BufferedReader in, BufferedWriter out){
//        try{
//            if(in != null){
//                in.close();
//            }
//            if(out != null){
//                out.close();
//            }
//            if(socket != null){
//                socket.close();
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }
//}
package com.example.groupgsecurityca.Client;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import com.example.groupgsecurityca.AES.AES_KEY;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;
    private String password;

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
            out.write(password);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            CloseEverything(socket, in, out);
        }
    }

    public void sendMessage(String message) {
        try{
//            out.write(username);
//            out.newLine();
//            out.flush();
//            out.write(password);
//            out.newLine();
//            out.flush();
            out.write(message);
            out.newLine();
            out.flush();
        }catch(Exception e){
            CloseEverything(socket, in, out);
        }
    }

    public void ListenForMessages(VBox vbox) {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    String message = msg;
                    Platform.runLater(() -> {
                        ClientViewController.addLabel(message, vbox);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
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
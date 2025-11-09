package com.example.groupgsecurityca.Server;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import com.example.groupgsecurityca.AES.AES_KEY;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ClientHandler implements Runnable{

    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

//    ClientHandler testClient = new ClientHandler("test", "123");


    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;
    private String clientPassword;

//    public ClientHandler(String username, String password) {
//        this.clientUsername = username;
//        this.clientPassword = password;
//    }

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.clientUsername = in.readLine();
//            this.clientPassword = in.readLine();

            clients.add(this);
            BroadCastMessage(clientUsername + ": " + "has joined the chat!");
            System.out.println(clientUsername + ": " + "has joined the Server!");
        }catch(IOException e){
            CloseEverything(socket, in, out);
        }
    }

//    @Override
//    public void run(){
//        try{
//            String recievedMessage;
//            while(socket.isConnected()){
//                recievedMessage = in.readLine();
//                System.out.println("the server received the message: " + recievedMessage);
//                BroadCastMessage(recievedMessage);
//            }
//        }catch(IOException e){
//            CloseEverything(socket, in, out);
//        }
//    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(clientUsername + ": " + message);
                BroadCastMessage(clientUsername + ": " + message);
            }
        } catch (Exception e) {
            CloseEverything(socket, in, out);
        }
    }


    public void BroadCastMessage(String message){
        synchronized(clients) {
            for (ClientHandler client : new ArrayList<>(clients)) {
                try {
                    if (!client.clientUsername.equals(this.clientUsername)) {
                        client.out.write(message);
                        client.out.newLine();
                        client.out.flush();
                    }
                } catch (IOException e) {
                    client.CloseEverything(client.socket, in, out);
                }
            }
        }
    }

    public void RemoveClient(){
        clients.remove(this);
        BroadCastMessage(clientUsername + ": " + "has left the chat!");
    }

    public void CloseEverything(Socket socket, BufferedReader in, BufferedWriter out){
        RemoveClient();
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

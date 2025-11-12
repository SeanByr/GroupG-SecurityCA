package com.example.groupgsecurityca.Server;

import com.example.groupgsecurityca.Security.HashingManager;
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
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private Socket socket;
    BufferedReader in;
    BufferedWriter out;
    String clientUsername;


    //constructor for creating/adding new clients to chat
    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //read username and password from client
            this.clientUsername = in.readLine();
            String clientPassword = in.readLine();

            //validate information and log hashing details
            if (!validateAndLogCredentials(clientUsername, clientPassword)) {
                System.out.println("[" + getDate() + "] Login failed for user '" + clientUsername + "'. Disconnecting.");
                out.write("Login failed");
                out.newLine();
                out.flush();
                catchEverything(socket, in, out);
                return;
            }

            /*
                Joshua Boyne (23343338)
                scans the existing clients list to prevent multiple logins with the same username
                if it does happen the log in is denied
                it will send a message to the clients and disconnects
             */
            for (ClientHandler existingClient : clients) {
                if (existingClient.clientUsername.equals(clientUsername)) {
                    System.out.println("[" + getDate() + "] Login denied: User '" + clientUsername + "' is already logged in. Disconnecting.");
                    out.write("Already logged in");
                    out.newLine();
                    out.flush();
                    catchEverything(socket, in, out);
                    return;
                }
            }

            clients.add(this);
            broadcastClientMessage("[" + getDate() + "] Server:" + clientUsername + "has entered the chatroom!");
            System.out.println("["+ getDate() + "]" + clientUsername + "Entered Server");

        }catch(IOException e){
            catchEverything(socket,in,out);
        }
    }

    /*
        Joshua Boyne (23343338)
        This method does the same as Login.validateUser() but it adds the users stored hash,salt and hash
        to the servers terminal
        This is just to see the hashing + salting working
     */
    private boolean validateAndLogCredentials(String username, String password) {

        String filePath = "/com/example/groupgsecurityca/data/users.txt";
        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("Error: users.txt not found in resources.");
                return false;
            }
            String line;
            String fileUser = null;
            String fileSalt = null;
            String fileHash = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    fileUser = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("Salt:")) {
                    fileSalt = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("Hash:")) {
                    fileHash = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("---------------------------")) {
                    if (fileUser != null && fileSalt != null && fileHash != null && fileUser.equals(username)) {

                        try {
                            String computedHash = HashingManager.hashKey(password, fileSalt);
                            boolean match = computedHash.equals(fileHash);
                            System.out.println("[" + getDate() + "] User '" + username + "' login attempt. Stored Hash: " + fileHash + ", Salt: " + fileSalt + ", Computed Hash: " + computedHash + ", Match: " + match);
                            return match;
                        } catch (Exception e) {
                            System.err.println("Error hashing password for '" + username + "': " + e.getMessage());
                            return false;
                        }
                    }
                    fileUser = null;
                    fileSalt = null;
                    fileHash = null;
                }
            }
            if (fileUser != null && fileSalt != null && fileHash != null && fileUser.equals(username)) {
                try {
                    String computedHash = HashingManager.hashKey(password, fileSalt);
                    boolean match = computedHash.equals(fileHash);
                    System.out.println("[" + getDate() + "] User '" + username + "' login attempt. Stored Hash: " + fileHash + ", Salt: " + fileSalt + ", Computed Hash: " + computedHash + ", Match: " + match);
                    return match;
                } catch (Exception e) {
                    System.err.println("Error hashing password for '" + username + "': " + e.getMessage());
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
            return false;
        }
        return false;
    }


    //takes in all received messages from clients to be broadcast
    //will change code when adding encryption/decryption

    @Override
    public void run() {
        String receivedMessage;

        while(socket.isConnected()){
            try{
                receivedMessage = in.readLine();

                broadcastClientMessage(receivedMessage);
            }catch(IOException e){
                catchEverything(socket,in,out);
                break;
            }
        }
    }

    //broadcast messages to all clients
    //might need to change based on encryption/decryption

    public void broadcastClientMessage(String message){
        try{
            for(ClientHandler clientHandler : clients){
                if(!clientHandler.clientUsername.equals(this.clientUsername)){

                    clientHandler.out.write(message);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            }
        }catch(IOException e){
            catchEverything(socket,in,out);
        }
    }

    public String getDate(){
        DateTimeFormatter datetf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return datetf.format(now);
    }

    //remove the clients when they close program#

    public void removeFromClientHandler(){
        clients.remove(this);
        broadcastClientMessage("[" + getDate() + "] Server:" + clientUsername + "has left the chatroom!");
        System.out.println("[" + getDate() + "]" + clientUsername + "Left the server");

    }

    //close the socket, buffreader and buffwriter
    public void catchEverything(Socket socket, BufferedReader in, BufferedWriter out){
        removeFromClientHandler();
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



//@Getter
//@Setter
//public class ClientHandler implements Runnable{
//
//    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
//
////    ClientHandler testClient = new ClientHandler("test", "123");
//
//
//    private Socket socket;
//    private BufferedReader in;
//    private BufferedWriter out;
//    private String clientUsername;
//    private String clientPassword;

//    public ClientHandler(String username, String password) {
//        this.clientUsername = username;
//        this.clientPassword = password;
//    }

//    public ClientHandler(Socket socket) {
//        try {
//            this.socket = socket;
//            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//
//            this.clientUsername = in.readLine();
////            this.clientPassword = in.readLine();
//
//            clients.add(this);
//            BroadCastMessage(clientUsername + ": " + "has joined the chat!");
//            System.out.println(clientUsername + ": " + "has joined the Server!");
//        }catch(IOException e){
//            CloseEverything(socket, in, out);
//        }
//    }

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

//    @Override
//    public void run() {
//        try {
//            String message;
//            while ((message = in.readLine()) != null) {
//                System.out.println(clientUsername + ": " + message);
//                BroadCastMessage(clientUsername + ": " + message);
//            }
//        } catch (Exception e) {
//            CloseEverything(socket, in, out);
//        }
//    }
//
//
//    public void BroadCastMessage(String message){
//        synchronized(clients) {
//            for (ClientHandler client : new ArrayList<>(clients)) {
//                try {
//                    if (!client.clientUsername.equals(this.clientUsername)) {
//                        client.out.write(message);
//                        client.out.newLine();
//                        client.out.flush();
//                    }
//                } catch (IOException e) {
//                    client.CloseEverything(client.socket, in, out);
//                }
//            }
//        }
//    }
//
//    public void RemoveClient(){
//        clients.remove(this);
//        BroadCastMessage(clientUsername + ": " + "has left the chat!");
//    }

//    public void CloseEverything(Socket socket, BufferedReader in, BufferedWriter out){
//        RemoveClient();
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

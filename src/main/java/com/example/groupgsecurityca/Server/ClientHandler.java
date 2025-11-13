package com.example.groupgsecurityca.Server;

import com.example.groupgsecurityca.Security.HashingManager;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import com.example.groupgsecurityca.AES.AES_KEY;
import com.example.groupgsecurityca.RSA.RSAEncryption;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>(); //keep track of clients
    public static Map<String, PublicKey> clientPublicKey = new HashMap<>();
    private Socket socket; //socket for connection to client
    BufferedReader in;
    BufferedWriter out;
    String clientUsername;
    private AES_KEY aes; // must be initiliazed to decrypt messages during broadcast
    private RSAEncryption rsa;

    //constructor for creating/adding new clients to list
    public ClientHandler(Socket socket, AES_KEY aes) {
        try{
            this.socket = socket;
            this.aes = aes;
            this.rsa = new RSAEncryption();
            //initialize input and output streams for client socket
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //take clients information and add them, then announce on server and console
            this.clientUsername = in.readLine();
            String clientPassword = in.readLine();

            /*

            Sean Byrne (23343362)
            retrieve the generated Public key from the Client and Store to in a hashMap

             */

            String clientPublicKeyBase64 = in.readLine();

            try{
                byte[] keyBytes = Base64.getDecoder().decode(clientPublicKeyBase64);
                PublicKey publicKey = java.security.KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
                clientPublicKey.put(clientUsername, publicKey);
                System.out.println("[" + getDate() + "][Server]: Stored " + clientUsername + "'s Public Key.");
            }catch(Exception e){
                System.out.println("[" + getDate() + "][Server]: failed to read Public Key for "
                                + clientUsername + ": " + e.getMessage());
            }

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
            broadcastClientMessage("[" + getDate() + "] Server: " + clientUsername + " has entered the chatroom!"); //could add timestamps
            System.out.println("["+ getDate() + "]" + clientUsername + " Entered Server");

        }catch(IOException e){
            catchEverything(socket,in,out);//clean resources and remove client when triggered
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

    @Override
    public void run() {
        String receivedMessage;

        while(socket.isConnected()){

            try{
                receivedMessage = in.readLine(); // encrypted from client

                System.out.println("[" + clientUsername + "]: " + receivedMessage);
                String decryptedMessage = receivedMessage;
                try {
                    decryptedMessage = aes.decrypt(receivedMessage);
                } catch(Exception ignored){}

//                System.out.println("[" + clientUsername + "]: " + decryptedMessage);

                broadcastClientMessage(receivedMessage); // send ENCRYPTED version to clients
            } catch(IOException e){
                catchEverything(socket,in,out);
                break;
            }
        }
    }

    //broadcast messages to all clients except sender

    public void broadcastClientMessage(String message){
        try{
            for(ClientHandler clientHandler : clients){
                if(!clientHandler.clientUsername.equals(this.clientUsername)){

                    if(message.contains("Server:")){
                        clientHandler.out.write(message);
                        clientHandler.out.newLine();
                        clientHandler.out.flush();
                    }else {

                        PublicKey publicKey = clientPublicKey.get(clientHandler.clientUsername);
                        byte[] encryptedAESKey = rsa.encryptKey(aes.getEncoded(), publicKey);
                        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);

                        clientHandler.out.write(encryptedKeyBase64 + "::" + message);
                        clientHandler.out.newLine();
                        clientHandler.out.flush();
                    }
                }
            }
        }catch(IOException e){
            catchEverything(socket,in,out);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //method to get current time for time user joins
    public String getDate(){
        DateTimeFormatter datetf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return datetf.format(now);
    }

    //remove the clients when they close program#

    public void removeFromClientHandler(){
        clients.remove(this);
        clientPublicKey.remove(this.clientUsername);
        broadcastClientMessage("[" + getDate() + "] Server:" + clientUsername + " has left the chatroom!");
        System.out.println("[" + getDate() + "]" + clientUsername + " Left the server");

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
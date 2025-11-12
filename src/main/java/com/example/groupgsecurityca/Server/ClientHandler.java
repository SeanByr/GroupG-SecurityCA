package com.example.groupgsecurityca.Server;

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

            this.clientUsername = in.readLine();
            clients.add(this);
            broadcastClientMessage("[" + getDate() + "] Server:" + clientUsername + "has entered the chatroom!"); //could add timestamps
            System.out.println("["+ getDate() + "]" + clientUsername + "Entered Server");

        }catch(IOException e){
            catchEverything(socket,in,out);
        }
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

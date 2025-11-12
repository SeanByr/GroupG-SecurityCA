package com.example.groupgsecurityca.Server;

import java.io.IOException;
import java.net.ServerSocket;
//start the server
public class ServerApplication {

    public static void main(String[] args) {
        final int PORT = 1234; //define port for our server

        try{
            System.out.println("Server starting");
            ServerSocket serverSocket = new ServerSocket(PORT);

            Server server = new Server(serverSocket);
            server.startServer();
        }catch(IOException e){
            System.out.println("Failed to start server");
            e.printStackTrace();
        }
    }
}

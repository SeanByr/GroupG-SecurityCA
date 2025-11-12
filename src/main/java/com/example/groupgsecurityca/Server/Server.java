package com.example.groupgsecurityca.Server;

import com.example.groupgsecurityca.AES.AES_KEY;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

public class Server {

    private final ServerSocket serverSocket;
    private static String aesKeyString;
    private AES_KEY aes;

    public Server(ServerSocket serverSocket) {

        this.serverSocket = serverSocket;

        try {
        // Generate AES key once
        AES_KEY aes = new AES_KEY();
        aes.init();
        byte[] keyBytes = aes.getEncoded(); // ref getEncoded, from AES for Server
        aesKeyString = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("[Server] AES key generated: " + aesKeyString);
    } catch (Exception e) {
        System.out.println("Failed to generate AES key");
        e.printStackTrace();
    }

    }



    //start server port 1234, listening for any potential users
    public void startServer() {
        try{
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connection");

                // SEnds the AES key string to client
                socket.getOutputStream().write((aesKeyString + "\n").getBytes());
                socket.getOutputStream().flush();


                ClientHandler clientHandler = new ClientHandler(socket, aes);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
            closeServerSocket(serverSocket);
        }
    }

    public void closeServerSocket(ServerSocket serverSocket) {
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}



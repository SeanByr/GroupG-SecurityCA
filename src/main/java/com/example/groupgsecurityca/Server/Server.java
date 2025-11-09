package com.example.groupgsecurityca.Server;

import com.example.groupgsecurityca.AES.AES_KEY;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }


    public void StartServer(){
        try{
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected to the Server!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
            CloseServerSocket(serverSocket);
        }
    }

    public void CloseServerSocket(ServerSocket serverSocket){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

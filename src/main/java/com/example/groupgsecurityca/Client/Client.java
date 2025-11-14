    package com.example.groupgsecurityca.Client;

    import com.example.groupgsecurityca.AES.AES_KEY;
    import com.example.groupgsecurityca.Controllers.GroupChatController;
    import com.example.groupgsecurityca.RSA.RSAEncryption;
    import javafx.application.Platform;
    import javafx.scene.layout.VBox;
    import com.example.groupgsecurityca.AES.AES_KEY;

    import java.io.*;
    import java.net.Socket;
    import java.util.Base64;

    /*
    Client Class - Sean Byrne 23343362
    Class that represents each Client connected to the server
    Class Used for sending and listening for messages from the group chat.
    Class handles secure communication by using RSA for key exchange and AES for message encryption/decryption

     */


    public class Client {

        private Socket socket;  //socket for establishing connect with the server and data transfer
        private BufferedReader in;  //reads incoming messages from the server
        private BufferedWriter out; //writes outgoing messages to the server
        private String username;
        private String password;
        private AES_KEY aes;    //AES key object for symmetric encryption/decryption of message in the group chat
        private RSAEncryption rsa;  //RSA key object for asymmetric key exchange

        /*
        constructor for Client class, initializes the client connection to the server
        sets up input and output streams, receives and initializes the AES key from the server
        then generates an RSA keypair for secure key exchange and sends the clients details such as:
        username, password and public key to the server for authentication and sharing of keys
         */
        public Client(Socket socket, String username, String password) {
            try {
                this.socket = socket;
                this.username = username;
                this.password = password;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String base64Key = in.readLine();
                byte[] keyBytes = Base64.getDecoder().decode(base64Key);

                aes = new AES_KEY();
                aes.initBytes(keyBytes); // get key bytes = base64 -- key
                System.out.println("[Client] AES key received and initialized."); // make sure is working
                // generates RSA keypair for each individual instance of the client
                this.rsa = new RSAEncryption();
                this.rsa.generateKeypair();;
                //sends username, password and base64 encoded public key to the server
                out.write(username);
                out.newLine();
                out.write(password);
                out.newLine();
                out.flush();
                out.write(this.rsa.getPublicKeyBase64());
                out.newLine();
                out.flush();

                System.out.println("RSA keypair generated, public key sent to server.");

            }catch(IOException e){
                CloseEverything(socket, in, out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // gets the message from the client-groupchat-view.fxml textfield
        // sends the message to the Server(ClientHandler class) to be broadcast to all clients
        public void SendMessage(String sendMessage){
            try {

                String encrypted = aes.encrypt(sendMessage); // encrypted messages when sending

                    out.write(encrypted);
                    out.newLine();
                    out.flush();
            }catch(IOException e){
                CloseEverything(socket, in, out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // separate thread that listens for messages broadcast by the Server(ClientHandler class)
        public void ListenForMessages(VBox vBox) {
            new Thread(() -> {
                    String receivedMessage = null;
                try {
                    while ((receivedMessage = in.readLine()) != null) {
                    try{

                        //split the received message into 2 segments: One for the encrypted AES key, other for the encrypted message
                        String[] msgSegments = receivedMessage.split("::", 2);

                        if (msgSegments.length == 2) {
                            //decode the base64 encoded encrypted AES eky
                            String encryptedAESKeyBase64 = msgSegments[0];
                            String encryptedMessage = msgSegments[1];

                            byte[] encryptedAESKeyBytes = Base64.getDecoder().decode(encryptedAESKeyBase64);
                            byte[] aesKeyBytes = rsa.decryptKey(encryptedAESKeyBytes);
                            aes.initBytes(aesKeyBytes);
                            String decryptedMessage = aes.decrypt(encryptedMessage);
                            //update the UI in the GroupChatController thread to display the decrypted message
                            Platform.runLater(() -> {
                                GroupChatController.addLabel(decryptedMessage, vBox);
                            });
                        } else {
                            String finalReceivedMessage = receivedMessage;
                            Platform.runLater(() ->{
                                GroupChatController.addLabel(finalReceivedMessage, vBox);
                            });
                        }


                    }catch(IllegalArgumentException e){
                        System.out.println("invalid base64 message received: " + receivedMessage);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }

                }catch (IOException e){
                        CloseEverything(socket, in, out);
                    }
            }).start();
        }

        /*
        method used to cleanly close all I/O and networking resources associated with the client
        prevents leaks and errors when a client disconnects
         */
        public void CloseEverything(Socket socket, BufferedReader in, BufferedWriter out){
            try{
                if(in != null){
                    in.close(); //close input stream
                }
                if(out != null){
                    out.close();    //close output stream
                }
                if(socket != null){
                    socket.close(); //close client socket connection
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }


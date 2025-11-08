package com.example.groupgsecurityca.AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

 /*
    Base Template for now before we go ahead...
    Generate a secret Key via AES(algorithm) ADVANCED ENCRYPTION STANDARD
    Encrypt message into readable bytes
    Decrypt the message into readable text
    Encode and Decode the data using Base64 for string handling
  */

public class AES_KEY {
    private SecretKey key;
    private int KEY_SIZE = 128;
    private int T_LEN = 128;
    private Cipher encryptionC;


    // Used to create encryption keys
    public void init() throws Exception{
        // Create a KeyGenrator to create random AES keys
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(KEY_SIZE);
        key = gen.generateKey();
    }

    public String encrypt(String message) throws Exception{

        return message;
    }

    public String decrypt(String encryptedMessages) throws Exception{

        return encryptedMessages;
    }

    private String encode (byte[] data) {

        return "";
    }

    private String decode (String data) {

        return data;
    }



}

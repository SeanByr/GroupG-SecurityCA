package com.example.groupgsecurityca.AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

 /*
    Base Template for now before we go ahead...
    Generate a secret Key via AES(algorithm) ADVANCED ENCRYPTION STANDARD
    Encrypt message into readable bytes
    Decrypt the message into readable text
    Encode and Decode the data using Base64 for string handling
  */

public class AES_KEY {
    private SecretKey key;          // var to store secret encryption key
    private int KEY_SIZE = 128;    // AES key size set to 128bits /key size constraints
    private int T_LEN = 128;        // Auth tag length for GCM (128)
    private Cipher encryptionC;     // Cipher Object

    // Used to create encryption keys
    public void init() throws Exception{
        // Create a KeyGenrator to create random AES keys
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(KEY_SIZE);
        key = gen.generateKey();
    }

    /*
    *   Convert Message into bytes
        Create a Cipher instance for AES-GCM encryption
        Initilaize the cipher with the encryption key
        Transform byte message into cipher text
        encode the ciphertext into Base64
    */

    public void initBytes(byte[] keyB) throws Exception{
        key = new SecretKeySpec(keyB, "AES");
    }

    public String encrypt(String message) throws Exception{

        encryptionC = Cipher.getInstance("AES/GCM/NoPadding"); // Cipher instance for AES algortihm in GCM no padding
        encryptionC.init(Cipher.ENCRYPT_MODE,key); // initialize using key

        byte[] messageInBytes = message.getBytes(); // take String message (input message) fromtext to byte array
        byte[] encryptedB = encryptionC.doFinal(messageInBytes); // transform message into encrypted bytes

        // Combine IV and CipherText so it can be reused
        byte[] iv = encryptionC.getIV();
        byte[] combinedIV = new byte[iv.length + encryptedB.length];
        // copy from array (iv) get index of source Pos (0), copy to destination (combinedIV), at destination Position index (0), length = whatever is in it
        System.arraycopy(iv, 0, combinedIV, 0, iv.length); // Just copying no encryption yet
        System.arraycopy(encryptedB, 0, combinedIV, iv.length, encryptedB.length); // Copy encrypted data

        return encode(combinedIV); // encode bytes to BASE64 strign and return
    }

    //Conor doing the decryption
    //decrypts AES encryped message
    public String decrypt(String encryptedMessages) throws Exception {
        byte[] combinedIV = decode(encryptedMessages); //convert string back into raw bytes
        byte[] iv = new byte[12];//the stored format
        System.arraycopy(combinedIV, 0, iv, 0, 12); // copy first 12 bytes

        byte[] cipherText = new byte[combinedIV.length - 12]; // - the IV size from encrypted data
        System.arraycopy(combinedIV, 12, cipherText, 0, cipherText.length); // only take the encrypted data first 12

        byte[] messageInBytes = decode(encryptedMessages);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");//initialize cipher instance

        // GCM param spec with Auth length, and same IV used during encryption
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN,iv);

        // Initalize Cipher decrypt in DECYPT MODE using key and IV spec above
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);

        // encrypted bytes to basic text
        byte[] decryptedBytes = decryptionCipher.doFinal(cipherText);
        return new String(decryptedBytes); // return decrypted bytes to String

    }

    // encode the ciphertext into Base64
    private String encode (byte[] data) { // byte array encodes Base64 and return
        return Base64.getEncoder().encodeToString(data);
    }

     // Export key bytes for server
    public byte[] getEncoded() {
        return key.getEncoded();
    }


    // decrypt original message
    private byte[] decode (String data) {
        return Base64.getDecoder().decode(data); // take Base64 string and decode into byte array and return
    }

}

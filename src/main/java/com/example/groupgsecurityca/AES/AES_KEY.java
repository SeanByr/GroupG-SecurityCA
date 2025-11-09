package com.example.groupgsecurityca.AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AES_KEY {
    private SecretKey key;          // var to store secret encryption key
    private int KEY_SIZE = 128;    // AES key size set to 128bits /key size constraints
    private int T_LEN = 128;        // Auth tag length for GCM (128)
    //    private Cipher encryptionC;     // Cipher Object
    private byte[] IV;

    // Used to create encryption keys
    public void init() throws Exception {
        // Create a KeyGenrator to create random AES keys
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(KEY_SIZE);
        key = gen.generateKey();

        IV = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
    }

    public void initFromStrings(String secretKey, String IV){
        key = new SecretKeySpec(decode(secretKey), "AES");
        this.IV = decode(IV);
    }

    /*
    *   Convert Message into bytes
        Create a Cipher instance for AES-GCM encryption
        Initilaize the cipher with the encryption key
        Transform byte message into cipher text
        encode the ciphertext into Base64
    */
    public String encryptOLD(String message) throws Exception {
        byte[] messageInBytes = message.getBytes(); // take String message (input message) fromtext to byte array
        Cipher encryptionC = Cipher.getInstance("AES/GCM/NoPadding"); // Cipher instance for AES algortihm in GCM no padding
        encryptionC.init(Cipher.ENCRYPT_MODE, key); // initialize using key
        IV = encryptionC.getIV();

        byte[] encryptedB = encryptionC.doFinal(messageInBytes); // transform message into encrypted bytes

        return encode(encryptedB); // encode bytes to BASE64 strign and return
    }

    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes(); // take String message (input message) fromtext to byte array
        Cipher encryptionC = Cipher.getInstance("AES/GCM/NoPadding"); // Cipher instance for AES algortihm in GCM no padding
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionC.init(Cipher.ENCRYPT_MODE, key, spec); // initialize using key
        IV = encryptionC.getIV();

        byte[] encryptedB = encryptionC.doFinal(messageInBytes); // transform message into encrypted bytes

        return encode(encryptedB); // encode bytes to BASE64 strign and return
    }

    public String decrypt(String encryptedMessages) throws Exception {

        byte[] messageInBytes = decode(encryptedMessages);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");

        // GCM param spec with Auth length, and same IV used during encryption
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);

        // Initalize Cipher decrypt in DECYPT MODE using key and IV spec above
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);

        // encrypted bytes to basic text
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes); // return decrypted bytes to String

    }

    // encode the ciphertext into Base64
    private String encode(byte[] data) { // byte array encodes Base64 and return
        return Base64.getEncoder().encodeToString(data);

    }

    // decrypt original message
    public byte[] decode(String data) { // take Base64 string and decode into byte array and return
        return Base64.getDecoder().decode(data);
    }

    public String getEncodedKey(){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String getEncodedIV(){
        return Base64.getEncoder().encodeToString(IV);
    }

    private void exportKeys(){
        System.out.println("SecretKey: " + encode(key.getEncoded()));
        System.out.println("IV: " + encode(IV));
    }

    public static void main(String[] args) {
        try {
            AES_KEY aes = new AES_KEY();
            aes.initFromStrings("8K4cIL63J+1IeDU0Umk6EQ==", "2eWo3CN/vjsAHRVJ");
            String encryptedMessage = aes.encrypt("HelloWorld");
            String decryptedMessage = aes.decrypt(encryptedMessage);

            System.out.println("Encrypted Message: " + encryptedMessage);
            System.out.println("Decrypted Message: " + decryptedMessage);

//            aes.exportKeys();
        } catch (Exception ignored) {
        }
    }
}

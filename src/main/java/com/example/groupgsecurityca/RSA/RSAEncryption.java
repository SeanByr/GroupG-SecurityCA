package com.example.groupgsecurityca.RSA;

/*

Sean Byrne (23343362)

This class first generates a public, private key value pair for each client
also handles the encryption and decryption
of the shared AES key used encrypt the messages send between the
clients within the in the application

 */

import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAEncryption {

    private KeyPair keyPair;

    public void generateKeypair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }

    public byte[] encryptKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public byte[] decryptKey(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE,  keyPair.getPrivate());
        return cipher.doFinal(data);
    }

    public String getPublicKeyBase64(){
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }
}

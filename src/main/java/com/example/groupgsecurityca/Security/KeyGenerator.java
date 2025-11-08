package com.example.groupgsecurityca.Security;

/*
    Joshua Boyne (23343338)

    *** Class Overview ***
    This class will generate salts, hash the users keys and will
    then store them in a map and then verify it during the authentication
    process

    - When a new key is created, it will generate the salt, hash the key and then
      store the salt + hash
    - For verifying, it will retrieve the salt + hash, ask for the input key
      call "HashingManager" and then respond success/fail
    - It can also handle the updating of keys, rotating salts and store metadata
 */

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class KeyGenerator {

    //class that represents the users information (salt, hash)
    private static class Keys{
        String salt;
        String hash;

        Keys(String salt, String hash){
            this.salt = salt;
            this.hash = hash;
        }
    }

    //HashMap that stores the users information that uses their username as the key
    private final Map<String, Keys> storedKeys = new HashMap<>();

    //method that registers a new user by generating a unique salt and hash for their key
    public void registerKey(String username, String key) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        String salt = SaltGenerator.generateSalt(); //generate random salt for user
        String hash = HashingManager.hashKey(key, salt); //hash the users key using the generated salt
        storedKeys.put(username, new Keys(salt, hash)); //store the salt and hash

        System.out.println("Registered: "+username+" with salt + hash");
    }

    //Verify users key with stored hash of user
    public boolean verifyKey(String username, String inputKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        Keys record = storedKeys.get(username); //look up the users key in the HashMap

        // if doesnt exist return false
        if(record == null){
            return false;
        }
        //compare the stored hash and input using HashingManager
        return HashingManager.verifyKey(inputKey, record.salt, record.hash);
    }
}

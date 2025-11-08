package com.example.groupgsecurityca.Security;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class SecurityTest {
    public static void main(String[]args) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException{
        KeyGenerator gen = new KeyGenerator();

        //register 2 users
        gen.registerKey("pat", "random123");
        gen.registerKey("michael", "ran123");

        //print their data and see stored salt and hash
        System.out.println("User Data");
        printUserData(gen, "pat");
        printUserData(gen, "michael");

        //verification tests
        System.out.println("TESTS");
        System.out.println("Pat correct key: "+gen.verifyKey("pat", "random123"));
        System.out.println("Pat wrong key: "+gen.verifyKey("pat", "random321"));
        System.out.println("Michael correct key: "+gen.verifyKey("michael", "ran123"));
        System.out.println("Michael wrong key: "+gen.verifyKey("michael", "ran321"));

    }

    // print the users salt and hash
    private static void printUserData(KeyGenerator gen, String username){
        try {
            //gets the private "storedKeys" from "KeyGenerator"
            var field = KeyGenerator.class.getDeclaredField("storedKeys");
            field.setAccessible(true);
            var storedKeys = (Map<String, ?>) field.get(gen);
            var record = storedKeys.get(username);

            if (record != null) {
                //get the salt and hash fileds
                var saltField = record.getClass().getDeclaredField("salt");
                var hashField = record.getClass().getDeclaredField("hash");
                saltField.setAccessible(true);
                hashField.setAccessible(true);

                //print the salt and hash
                System.out.println("User: " + username);
                System.out.println("Salt: " + saltField.get(record));
                System.out.println("Hash: " + hashField.get(record));
                System.out.println();
            } else {
                System.out.println("User not found: " + username);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
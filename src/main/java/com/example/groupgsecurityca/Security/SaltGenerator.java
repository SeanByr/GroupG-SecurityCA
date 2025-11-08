package com.example.groupgsecurityca.Security;

/*
    Joshua Boyne (23343338)

    This class will generate a random salt that will be added to
    each users key before its hashed

    - Will use a "SecureRandom" which is a cryptographic random generator
    - Decide on the salt length for more randomness (probably 16 bytes)
    - Then convert the salt into a string for storage
 */

import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {
    //length of the salt in bytes
    private static final int SALT_LENGTH = 16;

    /*
        This method will generate a random salt using "SecureRandom"
        The salt is then encoded as a base64 string so its easier to store and transmission
     */
    public static String generateSalt(){
        SecureRandom random = new SecureRandom(); //secure random generator
        byte[] salt = new byte[SALT_LENGTH]; //create an empty byte array
        random.nextBytes(salt); //fill it with random data
        return Base64.getEncoder().encodeToString(salt); //converts to string
    }

}

package com.example.groupgsecurityca.Security;

/*
    Joshua Boyne (23343338)

    The HashingManager class will make sure that the stored keys
    cant be retrieved or reused by someone else

    It will hash the users key using a cryptographic hash function and a salt
    It will then verify the users entered key and compare it to the stored hash

    - hashKey(key, salt) method will combine the key + salt, hash and encode result
    - verifyKey(inputKey, salt, storedHash) method will re-hash the inputKey + salt then
      compare it to the storedHash then return a boolean

 */

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class HashingManager {

    //PBKDF2 hashing algorithm and parameters
    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final int ITERATIONS = 65536; //number of iterations
    public static final int KEY_LENGTH = 256; //length of the generated hash in bits


    //hashing the key + salt
    public static String hashKey(String key, String salt) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        //converts the input into a PBE key spec (char array + decoded salt
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);

        //generate the hash
        SecretKeyFactory sfk = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = sfk.generateSecret(spec).getEncoded();
        //returns the hash in Base64 string
        return Base64.getEncoder().encodeToString(hash);
    }

    //verify the input key with the stored hash
    public static boolean verifyKey(String inputKey, String salt, String storedHash) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        String newHash = hashKey(inputKey, salt); // re-hash the input using the same salt
        return compareTime(storedHash, newHash); // use constant time comparison
    }

    /*
        This method will compare the 2 string in constant time to prevent timing attacks
        The timing attacks can happen when the comparison is stopped early on a mismatch
     */
    private static boolean compareTime(String a, String b){
        byte[] aByte = a.getBytes();
        byte[] bByte = b.getBytes();

        //fail if length is different
        if(aByte.length != bByte.length){
            return false;
        }

        int  result = 0;
        //XOR comparison that makes sure there is consistent timing regardless of where the mismatch happens
        for(int i = 0; i < aByte.length; i++){
            result |= aByte[i] ^ bByte[i];
        }
        return result == 0;
    }
}
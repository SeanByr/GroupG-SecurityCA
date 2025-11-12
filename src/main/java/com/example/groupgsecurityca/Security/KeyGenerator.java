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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
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
    private static final String USERS_FILE = "src/main/resources/com/example/groupgsecurityca/data/users.txt";

    public KeyGenerator() {
        try {
            loadUsersFromFile();
        } catch (IOException e) {
            System.out.println("No existing users file found. Starting fresh.");
        }
    }



    //method that registers a new user by generating a unique salt and hash for their key
    public void registerKey(String username, String key) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, IOException {
        String salt = SaltGenerator.generateSalt();
        String hash = HashingManager.hashKey(key, salt);
        storedKeys.put(username, new Keys(salt, hash));

        //save to file
        saveUserToFile(username, salt, hash);

        System.out.println("Registered: " + username + " with salt + hash");
    }

    //verify the users key with stored hash of user
    public boolean verifyKey(String username, String inputKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        Keys record = storedKeys.get(username);
        if (record == null) return false;

        return HashingManager.verifyKey(inputKey, record.salt, record.hash);
    }

    //save user to file
    private void saveUserToFile(String username, String salt, String hash) throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(username).append("\n");
        sb.append("Salt: ").append(salt).append("\n");
        sb.append("Hash: ").append(hash).append("\n");
        sb.append("Algorithm: PBKDF2WithHmacSHA256").append("\n");
        sb.append("Iterations: 65536").append("\n");
        sb.append("Created Date: ").append(LocalDate.now()).append("\n");
        sb.append("---------------------------\n");

        Files.write(file.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    //load users into memory when started up
    private void loadUsersFromFile() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        for (String line : Files.readAllLines(file.toPath())) {
            String[] parts = line.split("\\|");
            if (parts.length >= 3) {
                storedKeys.put(parts[0], new Keys(parts[1], parts[2]));
            }
        }
    }
}

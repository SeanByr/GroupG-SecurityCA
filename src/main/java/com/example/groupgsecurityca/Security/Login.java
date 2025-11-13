package com.example.groupgsecurityca.Security;

/*
    Joshua Boyne (23343338)
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Login {

    /*
        Checks if the username and password match the hardcoded users in users.txt file
        Reads the file and verifies the password by hashing it with the stored salt and compares
        it to the stored hash
        Returns true if valid or false if its invalid
     */
    public static boolean validateUser(String username, String password) {
        try (InputStream inputStream = Login.class.getResourceAsStream("/com/example/groupgsecurityca/data/users.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("Error: users.txt not found in resources.");
                return false;
            }

            String line;
            String fileUser = null;
            String fileSalt = null;
            String fileHash = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username:")) {
                    fileUser = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("Salt:")) {
                    fileSalt = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("Hash:")) {
                    fileHash = line.split(": ", 2)[1].trim();
                } else if (line.startsWith("---------------------------")) {
                    if (fileUser != null && fileSalt != null && fileHash != null) {
                        if (fileUser.equals(username)) {
                            try {
                                String newHash = HashingManager.hashKey(password, fileSalt);
                                return newHash.equals(fileHash);
                            } catch (Exception e) {
                                System.err.println("Error hashing password: " + e.getMessage());
                                return false;
                            }
                        }
                    }
                    fileUser = null;
                    fileSalt = null;
                    fileHash = null;
                }
            }

            /*
                processes the final user block in users.txt if it doesnt end with a separator
             */
            if (fileUser != null && fileSalt != null && fileHash != null && fileUser.equals(username)) {
                try {
                    String newHash = HashingManager.hashKey(password, fileSalt);
                    return newHash.equals(fileHash);
                } catch (Exception e) {
                    System.err.println("Error hashing password: " + e.getMessage());
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
            return false;
        }
        return false;
    }
}
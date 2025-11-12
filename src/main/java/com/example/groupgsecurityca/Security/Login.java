package com.example.groupgsecurityca.Security;

/*
    Joshua Boyne (23343338)
 */
import java.io.*;

public class Login {

    /*
        Checks if the username and password match the hardcoded users in users.txt file
        Reads the file and verifies the password by hashing it with the stored salt and compares
        it to the stored hash
        Returns true if valid or false if its invalid
     */
    public static boolean validateUser(String username, String password) {
//        try (InputStream inputStream = Login.class.getResourceAsStream("/com/example/groupgsecurityca/data/users.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            if (inputStream == null) {
//                System.err.println("Error: users.txt not found in resources.");
//                return false;
//            }

//            String line;
//            String fileUser = null;
//            String fileSalt = null;
//            String fileHash = null;
        String filePath = "src/main/resources/com/example/groupgsecurityca/data/users.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

    public static boolean saveUserToFile(UserRecord user) {
        String filePath = "/com/example/groupgsecurityca/data/users.txt";
        try (InputStream inputStream = Login.class.getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             FileWriter writer = new FileWriter("src/main/resources/com/example/groupgsecurityca/data/users.txt", true)) {  // Append mode
            if (inputStream == null) {
                System.err.println("Error: users.txt not found");
                return false;
            }
            // Read existing content to ensure proper appending
            StringBuilder existingContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                existingContent.append(line).append("\n");
            }
            // Append new user in the correct format
            writer.write("Username: " + user.getUsername() + "\n");
            writer.write("Salt: " + new String(user.getSalt()) + "\n");  // Assuming salt is Base64
            writer.write("Hash: " + new String(user.getHash()) + "\n");  // Assuming hash is Base64
            writer.write("Algorithm: " + user.getAlgorithm() + "\n");
            writer.write("Iterations: " + user.getIterations() + "\n");
            writer.write("Created Date: " + user.getCreatedDate() + "\n");
            writer.write("---------------------------\n");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user to users.txt: " + e.getMessage());
            return false;
        }
    }
}
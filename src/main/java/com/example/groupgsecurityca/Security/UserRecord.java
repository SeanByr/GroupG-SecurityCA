package com.example.groupgsecurityca.Security;

import java.time.LocalDate;
public class UserRecord {
    private final String username;
    private final byte[] salt;
    private final byte[] hash;
    private final int iterations;
    private final String algorithm;
    private final LocalDate createdDate;


    public UserRecord(String username, byte[] salt, byte[] hash, int iterations, String algorithm, LocalDate createdDate) {
        this.username = username;
        this.salt = salt;
        this.hash = hash;
        this.iterations = iterations;
        this.algorithm = algorithm;
        this.createdDate = createdDate;
    }

    public String getUsername() {
        return username;
    }
    public byte[] getSalt(){
        return salt;
    }
    public byte[] getHash(){
        return hash;
    }
    public int getIterations(){
        return iterations;
    }
    public String getAlgorithm(){
        return algorithm;
    }
    public LocalDate getCreatedDate(){
        return createdDate;
    }
}


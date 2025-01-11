package com.example.multilabmob.Models;

public class User {
    private String username;
    private String pwd;

    // Constructor
    public User(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

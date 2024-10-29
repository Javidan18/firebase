package com.example.sagliktakipandroid;

public class User {
    public String email;
    public String password;

    // Varsayılan yapıcı
    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}


package com.example.sagliktakipandroid;

public class User {
    public String userId; // Kullanıcı ID'si
    public String email;
    public String password;

    // Varsayılan yapıcı
    public User() {}

    // Kullanıcı yapıcısı
    public User(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
}

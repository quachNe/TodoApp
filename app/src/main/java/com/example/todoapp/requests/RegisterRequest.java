package com.example.todoapp.requests;

public class RegisterRequest {
    private String username;
    private String password;
    private String full_name;
    private String gender;
    private String email;

    public RegisterRequest(String username, String password,
                           String fullName, String gender, String email) {
        this.username = username;
        this.password = password;
        this.full_name = fullName;
        this.gender = gender;
        this.email = email;
    }
}

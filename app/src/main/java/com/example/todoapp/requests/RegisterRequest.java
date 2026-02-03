package com.example.todoapp.requests;

public class RegisterRequest {
    private String username;
    private String password;
    private String full_name;
    private String gender;

    public RegisterRequest(String username, String password,
                           String fullName, String gender) {
        this.username = username;
        this.password = password;
        this.full_name = fullName;
        this.gender = gender;
    }
}

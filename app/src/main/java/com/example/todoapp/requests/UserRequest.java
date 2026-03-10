package com.example.todoapp.requests;

public class UserRequest {
    private String fullName;
    private String gender;

    private String avatar;

    private String username;

    private String email;

    public UserRequest(String fullName, String gender, String avatar, String username, String email) {
        this.fullName = fullName;
        this.gender = gender;
        this.avatar = avatar;
        this.username = username;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() { return email; }
}
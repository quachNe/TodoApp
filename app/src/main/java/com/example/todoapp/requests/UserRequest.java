package com.example.todoapp.requests;

public class UserRequest {
    private String fullName;
    private String gender;

    private String avatar;

    private String username;

    public UserRequest(String fullName, String gender, String avatar, String username) {
        this.fullName = fullName;
        this.gender = gender;
        this.avatar = avatar;
        this.username = username;
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
}

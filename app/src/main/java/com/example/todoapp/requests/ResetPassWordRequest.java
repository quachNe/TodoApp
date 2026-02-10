package com.example.todoapp.requests;

import com.google.gson.annotations.SerializedName;

public class ResetPassWordRequest {

    private String username;

    @SerializedName("new_password")
    private String password;

    public ResetPassWordRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

package com.example.todoapp.responses;

import com.example.todoapp.models.User;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private boolean success;
    private String message;

    @SerializedName("access_token")
    private String accessToken;

    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }
}

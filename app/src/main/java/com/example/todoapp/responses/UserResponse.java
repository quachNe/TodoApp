package com.example.todoapp.responses;

import com.example.todoapp.models.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}

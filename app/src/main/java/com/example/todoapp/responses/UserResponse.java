package com.example.todoapp.responses;

import com.example.todoapp.models.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("msg")
    private String msg;

    @SerializedName("user")
    private User user;

    public String getMsg() {
        return msg;
    }

    public User getUser() {
        return user;
    }
}

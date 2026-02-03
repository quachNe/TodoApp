package com.example.todoapp.responses;

import com.example.todoapp.models.User;

public class RegisterResponse {
    private boolean success;
    private String message;
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}

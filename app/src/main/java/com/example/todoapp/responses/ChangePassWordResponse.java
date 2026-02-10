package com.example.todoapp.responses;

import com.google.gson.annotations.SerializedName;

public class ChangePassWordResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}

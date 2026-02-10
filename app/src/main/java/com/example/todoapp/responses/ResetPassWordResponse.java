package com.example.todoapp.responses;

public class ResetPassWordResponse {
    private boolean success;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ResetPassWordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}

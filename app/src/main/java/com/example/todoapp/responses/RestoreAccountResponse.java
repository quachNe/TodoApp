package com.example.todoapp.responses;

public class RestoreAccountResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}

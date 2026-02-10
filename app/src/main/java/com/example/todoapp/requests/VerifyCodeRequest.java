package com.example.todoapp.requests;

public class VerifyCodeRequest {
    private String username;
    private  String code;

    public VerifyCodeRequest(String username, String code) {
        this.username = username;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }
}

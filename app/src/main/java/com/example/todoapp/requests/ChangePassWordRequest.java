package com.example.todoapp.requests;

import com.google.gson.annotations.SerializedName;

public class ChangePassWordRequest {
    @SerializedName("old_password")
    private String oldPassWord;

    @SerializedName("new_password")
    private String newPassWord;

    public String getNewPassWord() {
        return newPassWord;
    }

    public String getOldPassWord() {
        return oldPassWord;
    }

    public ChangePassWordRequest(String oldPassWord, String newPassWord) {
        this.oldPassWord = oldPassWord;
        this.newPassWord = newPassWord;
    }
}

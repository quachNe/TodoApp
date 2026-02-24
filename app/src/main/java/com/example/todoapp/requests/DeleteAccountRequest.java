package com.example.todoapp.requests;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountRequest {
    @SerializedName("user_id")
    private int id;

    public DeleteAccountRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

package com.example.todoapp.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    private int id;

    @SerializedName("category_name")
    private String name;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("is_deleted")
    private boolean deleted;

    public Category() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

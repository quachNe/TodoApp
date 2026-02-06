package com.example.todoapp.requests;

import com.google.gson.annotations.SerializedName;

public class CategoryRequest {
    private int id;
    @SerializedName("category_name")
    private String name;

    public CategoryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

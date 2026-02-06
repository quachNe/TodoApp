package com.example.todoapp.responses;

import com.google.gson.annotations.SerializedName;

public class CategoryResponse {
    private int id;
    @SerializedName("category_name")
    private String name;

    @SerializedName("task_count")
    private int taskCount;
    public CategoryResponse() {}

    public CategoryResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTaskCount(){
        return taskCount;
    }
}

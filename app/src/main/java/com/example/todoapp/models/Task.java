package com.example.todoapp.models;

import com.google.gson.annotations.SerializedName;

public class Task {

    private int id;

    @SerializedName("task_name")
    private String taskName;

    private boolean completed;

    // ISO datetime string từ backend (vd: "2026-02-03T10:30:00")
    private String deadline;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("is_deleted")
    private boolean deleted;

    public Task() {}

    /* ===== Getter & Setter ===== */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

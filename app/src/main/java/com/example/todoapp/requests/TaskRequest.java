package com.example.todoapp.requests;

import com.google.gson.annotations.SerializedName;

public class TaskRequest {

    @SerializedName("task_name")
    private String taskName;

    @SerializedName("deadline")
    private String deadline;   // format: "2026-02-11T21:30:00"

    @SerializedName("completed")
    private Boolean completed;

    // Constructor dùng khi tạo task
    public TaskRequest(String taskName, String deadline) {
        this.taskName = taskName;
        this.deadline = deadline;
    }

    // Constructor rỗng (dùng cho update completed)
    public TaskRequest() {
    }

    // Getter
    public String getTaskName() {
        return taskName;
    }

    public String getDeadline() {
        return deadline;
    }

    public Boolean getCompleted() {
        return completed;
    }

    // Setter
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
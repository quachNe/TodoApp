package com.example.todoapp.responses;

import com.example.todoapp.models.Task;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TaskResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("summary")
    private Summary summary;

    @SerializedName("tasks")
    private List<Task> tasks;

    public String getMessage() {
        return message;
    }

    public Summary getSummary() {
        return summary;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // =========================
    // INNER CLASS SUMMARY
    // =========================
    public static class Summary {

        @SerializedName("total")
        private int total;

        @SerializedName("today")
        private int today;

        @SerializedName("completed")
        private int completed;

        @SerializedName("pending")
        private int pending;

        public int getTotal() {
            return total;
        }

        public int getToday() {
            return today;
        }

        public int getCompleted() {
            return completed;
        }

        public int getPending() {
            return pending;
        }
    }
}
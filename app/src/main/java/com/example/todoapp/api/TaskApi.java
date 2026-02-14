package com.example.todoapp.api;

import com.example.todoapp.requests.TaskRequest;
import com.example.todoapp.responses.TaskResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface TaskApi {

    // ======================== LẤY DANH SÁCH TASK CỦA MỘT CATEGORY ========================
    @GET("categories/{categoryId}/tasks")
    Call<TaskResponse> getTasksByCategory(@Path("categoryId") int categoryId);

    // ======================== THÊM TASK MỚI VÀO CATEGORY ========================
    @POST("categories/{categoryId}/tasks")
    Call<TaskResponse> createTask(
            @Path("categoryId") int categoryId,
            @Body TaskRequest request
    );

// ======================== CẬP NHẬT TASK ========================
    @PUT("tasks/{taskId}")
    Call<TaskResponse> updateTask(
            @Path("taskId") int taskId,
            @Body TaskRequest request
    );

    // ======================== XÓA TASK ========================
    @DELETE("tasks/{taskId}")
    Call<TaskResponse> deleteTask(@Path("taskId") int taskId);

}

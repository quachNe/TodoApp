package com.example.todoapp.api;

import com.example.todoapp.models.Task;
import com.example.todoapp.requests.TaskRequest;
import com.example.todoapp.responses.TaskResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface TaskApi {

    @GET("categories/{categoryId}/tasks")
    Call<TaskResponse> getTasksByCategory(@Path("categoryId") int categoryId);
    @POST("categories/{categoryId}/tasks")
    Call<TaskResponse> createTask(
            @Path("categoryId") int categoryId,
            @Body TaskRequest request
    );
    @DELETE("tasks/{taskId}")
    Call<TaskResponse> deleteTask(@Path("taskId") int taskId);

}

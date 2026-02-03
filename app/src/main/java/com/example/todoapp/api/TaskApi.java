package com.example.todoapp.api;

import com.example.todoapp.models.Task;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface TaskApi {

    @GET("tasks")
    Call<List<Task>> getTasks(
            @Header("Authorization") String token
    );

    @POST("tasks")
    Call<Task> createTask(
            @Header("Authorization") String token,
            @Body Task task
    );

    @PUT("tasks/{id}")
    Call<Task> updateTask(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Task task
    );

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(
            @Header("Authorization") String token,
            @Path("id") int id
    );
}

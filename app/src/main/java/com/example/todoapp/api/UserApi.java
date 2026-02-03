package com.example.todoapp.api;

import com.example.todoapp.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserApi {

    @GET("users")
    Call<User> getProfile(
            @Header("Authorization") String token
    );
}
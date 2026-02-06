package com.example.todoapp.api;

import com.example.todoapp.requests.LoginRequest;
import com.example.todoapp.requests.RegisterRequest;
import com.example.todoapp.responses.LoginResponse;
import com.example.todoapp.models.User;
import com.example.todoapp.responses.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

}

package com.example.todoapp.api;

import com.example.todoapp.models.User;
import com.example.todoapp.requests.CategoryRequest;
import com.example.todoapp.requests.ChangePassWordRequest;
import com.example.todoapp.requests.UserRequest;
import com.example.todoapp.responses.ChangePassWordResponse;
import com.example.todoapp.responses.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {

    @GET("api/users")
    Call<UserResponse> getProfile();


    @Multipart
    @PATCH("api/users")
    Call<UserResponse> updateProfile(
            @Part("full_name") RequestBody fullName,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part avatar
    );

    @PUT("api/users/change-password")
    Call<ChangePassWordResponse> changePass(
            @Body ChangePassWordRequest request
    );

}
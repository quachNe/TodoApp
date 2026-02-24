package com.example.todoapp.api;

import com.example.todoapp.requests.ChangePasswordRequest;
import com.example.todoapp.requests.RestoreAccountRequest;
import com.example.todoapp.responses.ChangePasswordResponse;
import com.example.todoapp.responses.DeleteAccountResponse;
import com.example.todoapp.responses.RestoreAccountResponse;
import com.example.todoapp.responses.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserApi {

    @GET("users")
    Call<UserResponse> getProfile();

    @Multipart
    @PATCH("users")
    Call<UserResponse> updateProfile(
            @Part("full_name") RequestBody fullName,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part avatar
    );

    @PUT("users/change-password")
    Call<ChangePasswordResponse> changePass(
            @Body ChangePasswordRequest request
    );

    @DELETE("users/delete-request")
    Call<DeleteAccountResponse> deleteAccount();
}
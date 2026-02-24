package com.example.todoapp.api;

import com.example.todoapp.requests.ForgotPasswordRequest;
import com.example.todoapp.requests.LoginRequest;
import com.example.todoapp.requests.RegisterRequest;
import com.example.todoapp.requests.ResetPassWordRequest;
import com.example.todoapp.requests.RestoreAccountRequest;
import com.example.todoapp.requests.VerifyCodeRequest;
import com.example.todoapp.responses.ForgotPasswordResponse;
import com.example.todoapp.responses.LoginResponse;
import com.example.todoapp.responses.RegisterResponse;
import com.example.todoapp.responses.ResetPassWordResponse;
import com.example.todoapp.responses.RestoreAccountResponse;
import com.example.todoapp.responses.VerifyCodeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    // ============================= ĐĂNG NHẬP USER =============================
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ============================= ĐĂNG KÝ TÀI KHOẢN =============================
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);


    // ============================= GỬI EMAIL LÀM MÃ =============================
    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(
            @Body ForgotPasswordRequest request
    );

    // ============================= XÁC NHẬN MÃ =============================
    @POST("auth/verify-reset-code")
    Call<VerifyCodeResponse> verifyCode(
            @Body VerifyCodeRequest request
    );

    // ============================= RESET MẬT KHẨU =============================
    @POST("auth/reset-password")
    Call<ResetPassWordResponse> resetPassWord(
            @Body ResetPassWordRequest request
    );

    @POST("auth/restore-account")
    Call<RestoreAccountResponse> restoreAccount(
            @Body RestoreAccountRequest request
    );
}

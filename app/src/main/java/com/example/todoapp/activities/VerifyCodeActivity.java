package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.requests.VerifyCodeRequest;
import com.example.todoapp.responses.VerifyCodeResponse;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends AppCompatActivity {

    private MaterialButton btnBack, btnVerify;
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private TextView tvOtpError;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        btnBack = findViewById(R.id.btnBack);
        btnVerify = findViewById(R.id.btnVerify);
        tvOtpError = findViewById(R.id.tvOtpError);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        loadingOverlay.bringToFront();

        btnBack.setOnClickListener(v -> finish());
        btnVerify.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {

        tvOtpError.setVisibility(View.GONE);

        String code =
                otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim() +
                        otp5.getText().toString().trim() +
                        otp6.getText().toString().trim();

        String username = getIntent().getStringExtra("username");

        // ❌ Không có username
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        // ❌ OTP chưa đủ
        if (code.length() != 6) {
            tvOtpError.setText("Vui lòng nhập đủ 6 chữ số");
            tvOtpError.setVisibility(View.VISIBLE);
            return;
        }

        // ✅ Chặn spam click
        btnVerify.setEnabled(false);

        showLoading();

        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        VerifyCodeRequest request = new VerifyCodeRequest(username, code);

        authApi.verifyCode(request).enqueue(new Callback<VerifyCodeResponse>() {

            @Override
            public void onResponse(Call<VerifyCodeResponse> call,
                                   Response<VerifyCodeResponse> response) {

                hideLoading();
                btnVerify.setEnabled(true);

                if (!response.isSuccessful()) {
                    Toast.makeText(
                            VerifyCodeActivity.this,
                            "Lỗi server (" + response.code() + ")",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                VerifyCodeResponse res = response.body();

                if (res == null) {
                    Toast.makeText(
                            VerifyCodeActivity.this,
                            "Dữ liệu phản hồi không hợp lệ",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if (res.isSuccess()) {

                    Toast.makeText(
                            VerifyCodeActivity.this,
                            res.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            VerifyCodeActivity.this,
                            ResetPasswordActivity.class
                    );

                    intent.putExtra("username", username);
                    startActivity(intent);

                } else {

                    tvOtpError.setText(res.getMessage());
                    tvOtpError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {

                hideLoading();
                btnVerify.setEnabled(true);

                Toast.makeText(
                        VerifyCodeActivity.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }
}
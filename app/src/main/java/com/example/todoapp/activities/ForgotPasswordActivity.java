package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.requests.ForgotPasswordRequest;
import com.example.todoapp.responses.ForgotPasswordResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton btnBack, btnSendCode;

    private TextInputEditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBack);
        btnSendCode = findViewById(R.id.btnSendCode);
        edtEmail = findViewById(R.id.edtEmail);

        String username = getIntent().getStringExtra("username");


        btnBack.setOnClickListener(v -> {
            finish();
        });


        btnSendCode.setOnClickListener(v -> callForgotPasswordApi(username));
    }


    private void callForgotPasswordApi(String username) {

        String email = edtEmail.getText() != null
                ? edtEmail.getText().toString().trim()
                : "";

        if (email.isEmpty()) {
            edtEmail.requestFocus();
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        btnSendCode.setEnabled(false);

        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        ForgotPasswordRequest request = new ForgotPasswordRequest(email, username);

        authApi.forgotPassword(request)
                .enqueue(new Callback<ForgotPasswordResponse>() {

                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call,
                                           Response<ForgotPasswordResponse> response) {

                        btnSendCode.setEnabled(true);
                        if (response.code() == 404) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "User không tồn tại trong hệ thống",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Lỗi server (" + response.code() + ")",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        ForgotPasswordResponse res = response.body();

                        if (res == null) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Dữ liệu phản hồi không hợp lệ",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (res.isSuccess()) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    res.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            // CHUYỂN SANG VERIFY CODE + GỬI DATA
                            Intent intent = new Intent(
                                    ForgotPasswordActivity.this,
                                    VerifyCodeActivity.class
                            );
                            intent.putExtra("username", username);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    res.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        btnSendCode.setEnabled(true);
                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Không kết nối được server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}
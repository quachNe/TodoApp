package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

import android.text.Editable;
import android.text.TextWatcher;

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

        setupOtpInputs();

        otp1.requestFocus();

        btnBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> handleSubmit());

        otp6.setOnEditorActionListener((v, actionId, event) -> {
            handleSubmit();
            return true;
        });
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

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() != 6) {
            tvOtpError.setText("Vui lòng nhập đủ 6 chữ số");
            tvOtpError.setVisibility(View.VISIBLE);
            return;
        }

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

    private void setupOtpInputs() {

        EditText[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < otpFields.length; i++) {

            final int index = i;

            otpFields[i].addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 1) {

                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        } else {
                            handleSubmit();
                        }
                    }

                    if (s.length() > 1) {

                        String pasted = s.toString();

                        if (pasted.length() == 6) {

                            for (int j = 0; j < 6; j++) {
                                otpFields[j].setText(String.valueOf(pasted.charAt(j)));
                            }

                            otp6.requestFocus();
                        }
                    }
                }
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {

                if (keyCode == KeyEvent.KEYCODE_DEL &&
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        otpFields[index].getText().toString().isEmpty() &&
                        index > 0) {

                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].setSelection(
                            otpFields[index - 1].getText().length()
                    );

                    return true;
                }

                return false;
            });
        }
    }

    private void showLoading() {

        loadingOverlay.setAlpha(0f);
        loadingOverlay.setVisibility(View.VISIBLE);

        loadingOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
    }

    private void hideLoading() {

        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> loadingOverlay.setVisibility(View.GONE))
                .start();
    }
}
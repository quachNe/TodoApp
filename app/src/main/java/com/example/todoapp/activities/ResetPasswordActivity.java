package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.requests.ResetPassWordRequest;
import com.example.todoapp.requests.VerifyCodeRequest;
import com.example.todoapp.responses.ResetPassWordResponse;
import com.example.todoapp.responses.VerifyCodeResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText edtNewPassword, edtConfirmPassword;
    private MaterialButton btnBack, btnConfirm;

    private TextView tvError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvError = findViewById(R.id.tvError);

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ResetPasswordActivity.this,
                    VerifyCodeActivity.class));
        });

        btnConfirm.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {

        String username = getIntent().getStringExtra("username");
        String txtNewPassword = edtNewPassword.getText().toString().trim();
        String txtConfirmPassword = edtConfirmPassword.getText().toString().trim();

        tvError.setVisibility(View.GONE);

        if (txtNewPassword.isEmpty()) {
            edtNewPassword.requestFocus();
            return;
        }

        if (txtConfirmPassword.isEmpty()) {
            edtConfirmPassword.requestFocus();
            return;
        }

        if (txtNewPassword.length() < 6) {
            tvError.setText("Mật khẩu phải có ít nhất 6 ký tự");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (!txtNewPassword.equals(txtConfirmPassword)) {
            tvError.setText("Mật khẩu không khớp");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        btnConfirm.setEnabled(false);

        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        ResetPassWordRequest request =
                new ResetPassWordRequest(username, txtNewPassword);

        authApi.resetPassWord(request)
                .enqueue(new Callback<ResetPassWordResponse>() {

                    @Override
                    public void onResponse(Call<ResetPassWordResponse> call,
                                           Response<ResetPassWordResponse> response) {

                        btnConfirm.setEnabled(true);

                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    "Lỗi server (" + response.code() + ")",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        ResetPassWordResponse res = response.body();

                        if (res == null) {
                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    "Dữ liệu phản hồi không hợp lệ",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (res.isSuccess()) {
                            new MaterialAlertDialogBuilder(ResetPasswordActivity.this)
                                    .setTitle("Thành công")
                                    .setMessage("Khôi phục mật khẩu thành công.\nVui lòng đăng nhập lại.")
                                    .setCancelable(false)
                                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                                        Intent intent = new Intent(
                                                ResetPasswordActivity.this,
                                                LoginActivity.class
                                        );

                                        // Xoá stack, không quay lại reset
                                        intent.setFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        );

                                        startActivity(intent);
                                        finish();
                                    })
                                    .show();
                        } else {
                            Toast.makeText(
                                    ResetPasswordActivity.this,
                                    res.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPassWordResponse> call, Throwable t) {
                        btnConfirm.setEnabled(true);
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "Không kết nối được server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

}
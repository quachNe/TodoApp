package com.example.todoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.UserApi;
import com.example.todoapp.requests.ChangePasswordRequest;
import com.example.todoapp.responses.ChangePasswordResponse;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private MaterialButton btnSaveChange;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // ========================== Ánh xạ View ========================
        btnBack = findViewById(R.id.btnBack);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSaveChange = findViewById(R.id.btnSaveChange);
        tvError = findViewById(R.id.tvError);

        // ========================== Xự kiện========================
        btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, Settings.class));
        });

        btnSaveChange.setOnClickListener(v -> {
            handleSubmit();
        });
    }
    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void handleSubmit() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // ===== VALIDATE =====
        if (oldPassword.isEmpty()) {
            edtOldPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            edtNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("Mật khẩu mới phải từ 6 ký tự");
            return;
        }

        if (newPassword.equals(oldPassword)) {
            showError("Mật khẩu mới không được trùng với mật khẩu cũ");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp");
            return;
        }

        tvError.setVisibility(View.GONE);

        // ===== CALL API =====
        UserApi userApi = ApiClient.getClient(this).create(UserApi.class);
        ChangePasswordRequest request =
                new ChangePasswordRequest(oldPassword, newPassword);

        userApi.changePass(request).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call,
                                   Response<ChangePasswordResponse> response) {

                if (response.code() == 401) {
                    showError("Mật khẩu cũ không đúng");
                    return;
                }

                if (!response.isSuccessful()) {
                    showError("Lỗi server (" + response.code() + ")");
                    return;
                }

                ChangePasswordResponse res = response.body();
                if (res == null) {
                    showError("Dữ liệu phản hồi không hợp lệ");
                    return;
                }

                if (res.isSuccess()) {
                    showSuccessDialog();
                } else {
                    showError(res.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                showError("Không kết nối được server");
            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu thành công")
                .setMessage("Vui lòng đăng nhập lại để tiếp tục.")
                .setPositiveButton("Đăng nhập", (d, which) -> {

                    // 1. Hủy session
                    SessionManager sessionManager =
                            new SessionManager(ChangePasswordActivity.this);
                    sessionManager.logout();

                    d.dismiss();

                    // 2. Quay về Login
                    Intent intent = new Intent(
                            ChangePasswordActivity.this,
                            LoginActivity.class
                    );
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .create();

        dialog.show();

        // đổi màu nút
        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setTextColor(Color.BLACK);
    }

}
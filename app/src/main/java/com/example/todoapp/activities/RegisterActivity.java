package com.example.todoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.requests.RegisterRequest;
import com.example.todoapp.responses.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // UI
    TextView tvLogin, tvError;
    TextInputEditText edtUsername, edtPassword, edtFullname;
    TextInputLayout tilUsername, tilPassword;
    MaterialButton btnRegister;

    RadioGroup rgGender;
    RadioButton rbMale, rbFemale, rbOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind view
        tvLogin = findViewById(R.id.tvLogin);
        tvError = findViewById(R.id.tvError);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtFullname = findViewById(R.id.edtFullname);

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);

        btnRegister = findViewById(R.id.btnRegister);

        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);

        // Quay về Login
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        // Click đăng ký
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {

        // Ẩn lỗi cũ
        tvError.setVisibility(View.GONE);
        tvError.setText("");

        String username = edtUsername.getText() != null
                ? edtUsername.getText().toString().trim()
                : "";

        String password = edtPassword.getText() != null
                ? edtPassword.getText().toString().trim()
                : "";

        String fullName = edtFullname.getText() != null
                ? edtFullname.getText().toString().trim()
                : "";

    /* =======================
       VALIDATE INPUT
       ======================= */
        if (fullName.isEmpty()) {
            edtFullname.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            edtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.requestFocus();
            return;
        }

    /* =======================
       GIỚI TÍNH
       ======================= */
        int checkedId = rgGender.getCheckedRadioButtonId();
        String gender = "";

        if (checkedId == R.id.rbMale) gender = "Nam";
        else if (checkedId == R.id.rbFemale) gender = "Nữ";
        else if (checkedId == R.id.rbOther) gender = "Khác";

        if (gender.isEmpty()) {
            showError("Vui lòng chọn giới tính");
            return;
        }

    /* =======================
       CALL API REGISTER
       ======================= */
        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        RegisterRequest request =
                new RegisterRequest(username, password, fullName, gender);

        authApi.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call,
                                   Response<RegisterResponse> response) {
                // Username đã tồn tại (BE trả 400)
                if (response.code() == 400) {
                    showError("Tên đăng nhập đã tồn tại");
                    return;
                }

                // Lỗi server khác
                if (!response.isSuccessful()) {
                    showError("Lỗi server (" + response.code() + ")");
                    return;
                }

                RegisterResponse res = response.body();

                if (res == null) {
                    showError("Phản hồi không hợp lệ");
                    return;
                }

                if (res.isSuccess()) {
                    showSuccessDialog();
                } else {
                    showError(res.getMessage());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showError("Không kết nối được server");
            }
        });
    }


    /* =======================
       HIỆN LỖI
       ======================= */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void showSuccessDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đăng ký thành công")
                .setMessage("Tài khoản của bạn đã được tạo.\nVui lòng đăng nhập để tiếp tục.")
                .setPositiveButton("Đăng nhập", (d, which) -> {
                    d.dismiss();
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

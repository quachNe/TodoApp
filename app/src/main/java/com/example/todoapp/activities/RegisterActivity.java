package com.example.todoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.requests.RegisterRequest;
import com.example.todoapp.responses.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // UI
    TextView tvLogin, tvError;
    TextView tvLength, tvUpper, tvNumber, tvSpecial;

    TextInputEditText edtUsername, edtPassword, edtFullname, edtEmail;
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

        tvLength = findViewById(R.id.tvLength);
        tvUpper = findViewById(R.id.tvUpper);
        tvNumber = findViewById(R.id.tvNumber);
        tvSpecial = findViewById(R.id.tvSpecial);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);

        btnRegister = findViewById(R.id.btnRegister);

        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);

        // realtime password check
        edtPassword.addTextChangedListener(passwordWatcher);

        // quay về login
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        btnRegister.setOnClickListener(v -> register());
    }

    /* =========================
       PASSWORD REALTIME CHECK
       ========================= */

    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String password = s.toString();

            updateRule(tvLength, password.length() >= 6);
            updateRule(tvUpper, password.matches(".*[A-Z].*"));
            updateRule(tvNumber, password.matches(".*\\d.*"));
            updateRule(tvSpecial, password.matches(".*[@$!%*?&].*"));
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void updateRule(TextView tv, boolean valid) {

        tv.setCompoundDrawableTintList(null); // giữ màu icon gốc

        if (valid) {
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check, 0, 0, 0);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_close, 0, 0, 0);
        }
    }

    /* =========================
       REGISTER
       ========================= */

    private void register() {

        tvError.setVisibility(View.GONE);
        tvError.setText("");

        String username = edtUsername.getText() != null ?
                edtUsername.getText().toString().trim() : "";

        String password = edtPassword.getText() != null ?
                edtPassword.getText().toString().trim() : "";

        String fullName = edtFullname.getText() != null ?
                edtFullname.getText().toString().trim() : "";

        String email = edtEmail.getText() != null ?
                edtEmail.getText().toString().trim() : "";

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

        if (email.isEmpty()) {
            edtEmail.requestFocus();
            return;
        }

        /* ===== gender ===== */

        int checkedId = rgGender.getCheckedRadioButtonId();
        String gender = "";

        if (checkedId == R.id.rbMale) gender = "Nam";
        else if (checkedId == R.id.rbFemale) gender = "Nữ";
        else if (checkedId == R.id.rbOther) gender = "Khác";

        if (gender.isEmpty()) {
            showError("Vui lòng chọn giới tính");
            return;
        }

        /* ===== CALL API ===== */

        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);

        RegisterRequest request =
                new RegisterRequest(username, password, fullName, gender, email);

        authApi.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call,
                                   Response<RegisterResponse> response) {

                if (response.code() == 400) {
                    try {

                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String message = jsonObject.getString("message");

                        showError(message);

                    } catch (Exception e) {
                        showError("Có lỗi xảy ra");
                    }

                    return;
                }

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

    /* =========================
       ERROR
       ========================= */

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    /* =========================
       SUCCESS DIALOG
       ========================= */

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

        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setTextColor(Color.BLACK);
    }
}
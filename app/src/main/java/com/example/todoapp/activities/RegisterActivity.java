package com.example.todoapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // UI
    TextView tvLogin, tvError;
    // Password hint
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

        // Quay về Login
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
        // Password realtime validate
        edtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String password = s.toString();

                boolean lengthValid = password.length() >= 6;
                boolean upperValid = password.matches(".*[A-Z].*");
                boolean numberValid = password.matches(".*[0-9].*");
                boolean specialValid = password.matches(".*[!@#$%^&*].*");

                checkCondition(lengthValid, tvLength);
                checkCondition(upperValid, tvUpper);
                checkCondition(numberValid, tvNumber);
                checkCondition(specialValid, tvSpecial);
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
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
        String email = edtEmail.getText() != null
                ? edtEmail.getText().toString().trim()
                : "";
    /* =======================
       VALIDATE INPUT
       ======================= */
        if (email.isEmpty()) {
            edtEmail.requestFocus();
            showError("Vui lòng nhập email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.requestFocus();
            showError("Email không hợp lệ");
            return;
        }
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
                new RegisterRequest(username, password, fullName, gender,email);

        authApi.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call,
                                   Response<RegisterResponse> response) {

                if (!response.isSuccessful()) {

                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject json = new JSONObject(errorBody);
                        String message = json.getString("message");

                        showError(message);

                    } catch (Exception e) {
                        showError("Lỗi server (" + response.code() + ")");
                    }

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

    private void checkCondition(boolean valid, TextView tv) {

        if (valid) {
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            tv.setTextColor(Color.parseColor("#16A34A"));
            tv.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#16A34A")));
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
            tv.setTextColor(Color.parseColor("#DC2626"));
            tv.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#DC2626")));
        }

    }
}

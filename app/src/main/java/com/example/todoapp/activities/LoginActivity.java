package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.AuthApi;
import com.example.todoapp.models.User;
import com.example.todoapp.requests.LoginRequest;
import com.example.todoapp.responses.LoginResponse;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView tvRegister, tvError;
    TextInputEditText edtUsername, edtPassword;
    TextInputLayout tilUsername, tilPassword;
    MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvRegister = findViewById(R.id.tvRegister);
        tvError = findViewById(R.id.tvError);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {

        // Ẩn lỗi cũ
        tvError.setVisibility(View.GONE);
        tvError.setText("");

        String username = edtUsername.getText() != null
                ? edtUsername.getText().toString().trim()
                : "";

        String password = edtPassword.getText() != null
                ? edtPassword.getText().toString().trim()
                : "";

        /* =======================
           BỎ TRỐNG → CHỈ FOCUS
           ======================= */
        if (username.isEmpty()) {
            edtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.requestFocus();
            return;
        }

        /* =======================
           CALL API LOGIN
           ======================= */
        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        LoginRequest request = new LoginRequest(username, password);

        authApi.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                // ❌ Sai tài khoản / mật khẩu
                if (response.code() == 401) {
                    showError("Sai tài khoản hoặc mật khẩu");
                    return;
                }

                // ❌ Lỗi khác (500, 404...)
                if (!response.isSuccessful()) {
                    showError("Lỗi server (" + response.code() + ")");
                    return;
                }

                LoginResponse res = response.body();

                if (res == null) {
                    showError("Dữ liệu phản hồi không hợp lệ");
                    return;
                }

                if (res.isSuccess()) {
                    SessionManager session = new SessionManager(LoginActivity.this);

                    session.saveSession(res.getAccessToken(), res.getUser().getId());

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    // phòng hờ (trường hợp backend trả 200 nhưng success=false)
                    String msg = res.getMessage();
                    if (msg == null || msg.isEmpty()) {
                        msg = "Sai tài khoản hoặc mật khẩu";
                    }
                    showError(msg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN", "CONNECT ERROR", t);
                showError("Không kết nối được server");
            }
        });
    }

    /* =======================
       HIỆN LỖI ĐỎ TRÊN NÚT LOGIN
       ======================= */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}

package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        btnBack = findViewById(R.id.btnBack);
        btnVerify = findViewById(R.id.btnVerify);
        tvOtpError = findViewById(R.id.tvOtpError);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnVerify.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        btnVerify.setEnabled(false);
        tvOtpError.setVisibility(View.GONE);

        String code =
                otp1.getText().toString().trim() +
                        otp2.getText().toString().trim() +
                        otp3.getText().toString().trim() +
                        otp4.getText().toString().trim() +
                        otp5.getText().toString().trim() +
                        otp6.getText().toString().trim();
        String username = getIntent().getStringExtra("username");

        if (code.length() != 6) {
            tvOtpError.setText("Vui lòng nhập đủ 6 chữ số");
            tvOtpError.setVisibility(View.VISIBLE);
            return;
        }

        AuthApi authApi = ApiClient.getClient(this).create(AuthApi.class);
        VerifyCodeRequest request = new VerifyCodeRequest(username,code);

        authApi.verifyCode(request)
                .enqueue(new Callback<VerifyCodeResponse>() {

                             @Override
                             public void onResponse(Call<VerifyCodeResponse> call,
                                                    Response<VerifyCodeResponse> response) {

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

                                     // 👉 CHUYỂN SANG VERIFY CODE + GỬI DATA
                                     Intent intent = new Intent(
                                             VerifyCodeActivity.this,
                                             ResetPasswordActivity.class
                                     );
                                     intent.putExtra("username", username);

                                     startActivity(intent);
                                 } else {
                                     Toast.makeText(
                                             VerifyCodeActivity.this,
                                             res.getMessage(),
                                             Toast.LENGTH_SHORT
                                     ).show();
                                 }
                             }

                             @Override
                             public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {
                                 btnVerify.setEnabled(true);
                                 Toast.makeText(
                                         VerifyCodeActivity.this,
                                         "Không kết nối được server",
                                         Toast.LENGTH_SHORT
                                 ).show();
                             }
                         }
                );
    }
}
package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.UserApi;
import com.example.todoapp.responses.DeleteAccountResponse;
import com.example.todoapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    ImageView btnBack;

    LinearLayout btnProfile, btnChangePass, btnLogout, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ id
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);


        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnChangePass.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnDeleteAccount.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Xóa tài khoản")
                    .setMessage("Tài khoản sẽ bị xóa vĩnh viễn sau 15 ngày. Bạn có chắc không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {

                        UserApi api = ApiClient
                                .getClient(this)
                                .create(UserApi.class);

                        api.deleteAccount().enqueue(new Callback<DeleteAccountResponse>() {

                            @Override
                            public void onResponse(Call<DeleteAccountResponse> call,
                                                   Response<DeleteAccountResponse> response) {

                                if (response.isSuccessful() && response.body() != null) {

                                    Toast.makeText(
                                            SettingsActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    // Logout sau khi yêu cầu xóa
                                    SessionManager session =
                                            new SessionManager(SettingsActivity.this);
                                    session.logout();

                                    Intent intent = new Intent(
                                            SettingsActivity.this,
                                            LoginActivity.class
                                    );
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(
                                            SettingsActivity.this,
                                            "Không thể yêu cầu xóa tài khoản",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteAccountResponse> call,
                                                  Throwable t) {

                                Toast.makeText(
                                        SettingsActivity.this,
                                        "Lỗi kết nối server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });

                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

}
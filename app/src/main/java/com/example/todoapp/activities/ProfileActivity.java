package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.R;

public class ProfileActivity extends AppCompatActivity {
    ImageView btnBack;
    TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // ========================== Ánh xạ View ========================
        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);


        btnBack.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, Settings.class));
        });
    }
}
package com.example.todoapp;

import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.se.omapi.Session;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.activities.HomeActivity;
import com.example.todoapp.activities.LoginActivity;
import com.example.todoapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
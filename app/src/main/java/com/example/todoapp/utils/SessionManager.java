package com.example.todoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "todo_app";
    private static final String TOKEN_KEY = "token";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Lưu JWT
    public void saveToken(String token) {
        prefs.edit().putString(TOKEN_KEY, token).apply();
    }

    // Lấy JWT
    public String getToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    // Kiểm tra đã login chưa
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Logout
    public void logout() {
        prefs.edit().clear().apply();
    }
}
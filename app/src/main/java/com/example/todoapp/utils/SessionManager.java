package com.example.todoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todoapp.models.User;

public class SessionManager {
    private static final String PREF_NAME = "todo_app";

    private static final String TOKEN_KEY = "token";
    private static final String USER_ID = "user_id";
    private static final String USERNAME = "username";
    private static final String FULL_NAME = "full_name";
    private static final String AVATAR_URL = "avatar_url";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ✅ Lưu toàn bộ user
    public void saveUser(String token, User user) {
        prefs.edit()
                .putString(TOKEN_KEY, token)
                .putInt(USER_ID, user.getId())
                .putString(USERNAME, user.getUsername())
                .putString(FULL_NAME, user.getFullName())
                .putString(AVATAR_URL, user.getAvatarUrl())
                .apply();
    }

    public String getToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    public String getAvatarUrl() {
        return prefs.getString(AVATAR_URL, null);
    }

    public String getFullName() {
        return prefs.getString(FULL_NAME, "");
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}

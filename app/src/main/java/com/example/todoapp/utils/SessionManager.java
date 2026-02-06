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

    // ✅ Lưu user + token
    public void saveUser(String token, User user) {
        prefs.edit()
                .putString(TOKEN_KEY, token)
                .putInt(USER_ID, user.getId())
                .putString(USERNAME, user.getUsername())
                .putString(FULL_NAME, user.getFullName())
                .putString(AVATAR_URL, user.getAvatarUrl())
                .apply();
    }

    // ✅ TOKEN
    public String getAccessToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    // ✅ USER ID
    public int getUserId() {
        return prefs.getInt(USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(USERNAME, "");
    }

    public String getFullName() {
        return prefs.getString(FULL_NAME, "");
    }

    public String getAvatarUrl() {
        return prefs.getString(AVATAR_URL, null);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}

package com.example.todoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONObject;

public class SessionManager {

    private static final String PREF_NAME = "todo_app";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID = "user_id";

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, int userId) {
        prefs.edit()
                .putString(TOKEN_KEY, token)
                .putInt(USER_ID, userId)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(TOKEN_KEY, null);
    }

    public int getUserId() {
        return prefs.getInt(USER_ID, -1);
    }

    // ✅ CHECK LOGIN + TOKEN EXPIRE
    public boolean isLoggedIn() {
        String token = getAccessToken();
        if (token == null) return false;

        if (isTokenExpired(token)) {
            logout(); // clear token hết hạn
            return false;
        }

        return true;
    }

    // ✅ Decode JWT để check exp
    private boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;

            String payload = new String(
                    Base64.decode(parts[1], Base64.URL_SAFE)
            );

            JSONObject json = new JSONObject(payload);
            long exp = json.getLong("exp"); // seconds

            long now = System.currentTimeMillis() / 1000;
            return now >= exp;

        } catch (Exception e) {
            return true; // lỗi decode => coi như hết hạn
        }
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}

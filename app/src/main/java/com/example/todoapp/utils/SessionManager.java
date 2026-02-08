package com.example.todoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todoapp.api.ApiClient;
import com.example.todoapp.models.User;

public class SessionManager {

//    private static final String PREF_NAME = "todo_app";
//
//    private static final String TOKEN_KEY = "token";
//    private static final String USER_ID = "user_id";
//    private static final String USERNAME = "username";
//    private static final String FULL_NAME = "full_name";
//    private static final String AVATAR_URL = "avatar_url";
//    private static final String AVATAR = "avatar";
//
//    private static final String Gender = "gender";
//
//
//    private SharedPreferences prefs;
//
//    public SessionManager(Context context) {
//        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//    }
//    public void saveAvatarUrl(String url) {
//        prefs.edit()
//                .putString("avatar_url", url)
//                .apply();
//    }
//
//    // ✅ Lưu user + token
//    public void saveUser(String token, User user) {
//        String avatarUrl = user.getAvatarUrl();
//
//        if (avatarUrl != null && avatarUrl.startsWith("/")) {
//            avatarUrl = ApiClient.BASE_URL + avatarUrl;
//        }
//
//        prefs.edit()
//                .putString(TOKEN_KEY, token)
//                .putInt(USER_ID, user.getId())
//                .putString(USERNAME, user.getUsername())
//                .putString(FULL_NAME, user.getFullName())
//                .putString(Gender, user.getGender())
//                .putString(AVATAR_URL, avatarUrl)
//                .putString(AVATAR, user.getAvatar())
//                .apply();
//    }
//
//
//    // ✅ TOKEN
//    public String getAccessToken() {
//        return prefs.getString(TOKEN_KEY, null);
//    }
//
//    // ✅ USER ID
//    public int getUserId() {
//        return prefs.getInt(USER_ID, -1);
//    }
//
//    public String getUsername() {
//        return prefs.getString(USERNAME, "");
//    }
//
//    public String getFullName() {
//        return prefs.getString(FULL_NAME, "");
//    }
//
//    public String getAvatarUrl() {
//        return prefs.getString(AVATAR_URL, null);
//    }
//
//    public String getGender(){
//        return prefs.getString(Gender, "");
//    }
//
//    public String getAvatar() {
//        return prefs.getString(AVATAR, null);
//    }
//
//
//    public boolean isLoggedIn() {
//        return getAccessToken() != null;
//    }
//
//    public void logout() {
//        prefs.edit().clear().apply();
//    }


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

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}

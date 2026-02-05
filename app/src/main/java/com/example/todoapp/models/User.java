package com.example.todoapp.models;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    private String username;
    private String password;

    @SerializedName("full_name")
    private String fullName;

    private String gender;

    private String avatar;
    @SerializedName("avatar_url")
    private String avatarUrl; // 👈 thêm đúng 1 dòng này
    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
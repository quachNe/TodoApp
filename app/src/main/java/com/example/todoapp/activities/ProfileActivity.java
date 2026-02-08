package com.example.todoapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.UserApi;
import com.example.todoapp.models.User;
import com.example.todoapp.responses.UserResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView btnBack, imgAvatar, btnProfile;
    private TextView txtUsernameValue, txtGenderValue, btnEditAvatar, txtFullname;

    private View cardEditName;
    private View overlay;
    private TextInputEditText edtEditFullname;

    private Button btnCancelEditName, btnSaveEditName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // ============================ Ánh xạ View ======================
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtFullname = findViewById(R.id.txtFullname);
        txtUsernameValue = findViewById(R.id.txtUsernameValue);
        txtGenderValue = findViewById(R.id.txtGenderValue);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        btnProfile = findViewById(R.id.btnProfile);
        edtEditFullname = findViewById(R.id.edtEditFullname);
        cardEditName = findViewById(R.id.cardEditName);
        overlay = findViewById(R.id.overlay);
        btnCancelEditName = findViewById(R.id.btnCancelEditName);
        btnSaveEditName = findViewById(R.id.btnSaveEditName);


        // ============================ Innit ======================
        loadUser();

        // ============================ Sự kiện ======================
        btnBack.setOnClickListener(v -> finish());
        btnEditAvatar.setOnClickListener(this::showAvatarPopup);
        btnProfile.setOnClickListener(v -> {
            edtEditFullname.setText(txtFullname.getText().toString());
            cardEditName.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
        });
        btnCancelEditName.setOnClickListener(v -> {
            cardEditName.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        });
        btnSaveEditName.setOnClickListener(v -> handleSubmit());


    }

    private void loadUser() {
        UserApi api = ApiClient.getClient(this).create(UserApi.class);

        api.getProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call,
                                   Response<UserResponse> response) {

                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().getUser() == null) {

                    imgAvatar.setImageResource(R.drawable.ic_avatar);
                    return;
                }

                User user = response.body().getUser();

                // Text info
                txtFullname.setText(user.getFullName());
                txtGenderValue.setText(user.getGender());
                txtUsernameValue.setText(user.getUsername());

                // Avatar
                String avatarUrl = user.getAvatar();

                Glide.with(ProfileActivity.this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .circleCrop()
                        .into(imgAvatar);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                imgAvatar.setImageResource(R.drawable.ic_avatar);
            }
        });
    }

    private void showAvatarPopup(View anchorView) {
        View popupView = getLayoutInflater()
                .inflate(R.layout.popup_choosen_avatar, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupView.findViewById(R.id.btnGallery).setOnClickListener(v -> {
            popupWindow.dismiss();
            openGallery();
        });

        popupView.findViewById(R.id.btnCamera).setOnClickListener(v -> {
            popupWindow.dismiss();
            Toast.makeText(this, "Camera chưa làm", Toast.LENGTH_SHORT).show();
        });

        popupWindow.setElevation(12f);
        popupWindow.showAsDropDown(anchorView, -80, 10);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            Uri imageUri = data.getData();
            if (imageUri == null) return;

            // preview ngay
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(imgAvatar);

            uploadAvatar(imageUri);
        }
    }

    private void uploadAvatar(Uri uri) {
        File file = uriToFile(uri);
        if (file == null || !file.exists()) {
            Toast.makeText(this,
                    "Không đọc được ảnh",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody imageBody =
                RequestBody.create(file, MediaType.parse("image/*"));

        MultipartBody.Part avatarPart =
                MultipartBody.Part.createFormData(
                        "avatar",
                        file.getName(),
                        imageBody
                );

        UserApi api =
                ApiClient.getClient(this).create(UserApi.class);

        api.updateProfile(null, null, avatarPart)
                .enqueue(new Callback<UserResponse>() {

                    @Override
                    public void onResponse(Call<UserResponse> call,
                                           Response<UserResponse> response) {

                        if (response.isSuccessful()) {
                            // 🔥 reload lại user từ server
                            loadUser();
                            Toast.makeText(ProfileActivity.this,
                                    "Cập nhật avatar thành công",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                "Upload thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File uriToFile(Uri uri) {
        try {
            File file = new File(
                    getCacheDir(),
                    "avatar_" + System.currentTimeMillis() + ".jpg"
            );

            InputStream in = getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleSubmit() {
        String fullName = edtEditFullname.getText().toString().trim();

        if (fullName.isEmpty()) {
            edtEditFullname.setError("Không được để trống");
            edtEditFullname.requestFocus();
            return;
        }

        // 1️⃣ Tạo RequestBody cho full_name
        RequestBody fullNameBody =
                RequestBody.create(fullName, MediaType.parse("text/plain"));

        // 2️⃣ gender (nếu chưa đổi thì gửi giá trị hiện tại)
        RequestBody genderBody = null;

        // 3️⃣ avatar (KHÔNG đổi avatar → gửi null)
        MultipartBody.Part avatarPart = null;

        // 4️⃣ Gọi API
        UserApi api = ApiClient.getClient(this).create(UserApi.class);
        Call<UserResponse> call = api.updateProfile(
                fullNameBody,
                genderBody,
                avatarPart
        );

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    cardEditName.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                    txtFullname.setText(fullName);
                    Toast.makeText(ProfileActivity.this,
                            "Cập nhật thông tin thành công",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Cập nhật thất bại",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
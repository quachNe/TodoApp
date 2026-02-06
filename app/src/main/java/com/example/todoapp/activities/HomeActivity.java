package com.example.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.CategoryApi;
import com.example.todoapp.requests.CategoryRequest;
import com.example.todoapp.responses.CategoryResponse;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnMenu;
    private TextView txtDate;

    private FloatingActionButton btnAddCategory;
    private EditText edtSearch, edtAddTask;
    private CardView cardAddTask, cardSearch;
    private View overlay;

    private GridLayout gridCategory;
    private ImageView imgEmptyCategory;
    private TextView txtEmptyTitle, txtEmptyHint;

    private SessionManager session;
    private CategoryApi categoryApi;

    private ImageButton btnSendTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* ================= ÁNH XẠ VIEW ================= */
        imgAvatar = findViewById(R.id.imgAvatar);
        btnMenu = findViewById(R.id.btnMenu);
        txtDate = findViewById(R.id.txtDate);

        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnSendTask = findViewById(R.id.btnSendTask);
        cardAddTask = findViewById(R.id.cardAddTask);
        cardSearch = findViewById(R.id.cardSearch);
        edtSearch = findViewById(R.id.edtSearch);
        edtAddTask = findViewById(R.id.edtAddTask);
        overlay = findViewById(R.id.overlay);

        gridCategory = findViewById(R.id.gridCategory);
        imgEmptyCategory = findViewById(R.id.imgEmptyCategory);
        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);
        txtEmptyHint = findViewById(R.id.txtEmptyHint);

        /* ================= INIT ================= */
        session = new SessionManager(this);
        categoryApi = ApiClient.getClient(this).create(CategoryApi.class);

        setCurrentDate();
        loadUser();
        loadCategories(); // ⭐ GỌI API Ở ĐÂY

        btnAddCategory.setOnClickListener(v -> toggleAddTask());

        btnMenu.setOnClickListener(v ->
                startActivity(new Intent(this, Settings.class))
        );

        btnSendTask.setOnClickListener(v -> {
            String name = edtAddTask.getText().toString();
            addCategory(name);
        });


        overlay.setOnClickListener(v -> closeAddTask());
    }

    /* ================= DATE ================= */
    private void setCurrentDate() {
        Date now = new Date();
        Locale localeVN = new Locale("vi", "VN");
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE, d 'Tháng' M yyyy", localeVN);

        String date = sdf.format(now);
        txtDate.setText(
                date.substring(0, 1).toUpperCase() + date.substring(1)
        );
    }

    /* ================= USER ================= */
    private void loadUser() {
        String avatarUrl = session.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }

    /* ================= ADD CATEGORY UI ================= */
    private void toggleAddTask() {
        if (cardAddTask.getVisibility() == View.GONE) {
            cardAddTask.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            lockSearch(true);
        } else {
            closeAddTask();
        }
    }

    private void closeAddTask() {
        cardAddTask.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        lockSearch(false);
    }

    private void lockSearch(boolean lock) {
        cardSearch.setEnabled(!lock);
        edtSearch.setEnabled(!lock);
        edtSearch.setFocusable(!lock);
        edtSearch.setFocusableInTouchMode(!lock);
        cardSearch.setAlpha(lock ? 0.4f : 1f);
    }

    /* ================= LOAD CATEGORY API ================= */
    private void loadCategories() {

        if (!session.isLoggedIn()) {
            showEmptyCategory(true);
            return;
        }

        categoryApi.getMyCategories()
                .enqueue(new Callback<List<CategoryResponse>>() {
                    @Override
                    public void onResponse(
                            Call<List<CategoryResponse>> call,
                            Response<List<CategoryResponse>> response
                    ) {

                        if (response.isSuccessful() && response.body() != null) {
                            loadCategoryToGrid(response.body());
                        } else {
                            showEmptyCategory(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                        showEmptyCategory(true);
                    }
                });
    }


    /* ================= GRID CATEGORY ================= */
    private void loadCategoryToGrid(List<CategoryResponse> categories) {
        gridCategory.removeAllViews();

        if (categories == null || categories.isEmpty()) {
            showEmptyCategory(true);
            return;
        }

        showEmptyCategory(false);

        for (CategoryResponse category : categories) {
            View item = getLayoutInflater()
                    .inflate(R.layout.item_category, gridCategory, false);

            TextView txtName = item.findViewById(R.id.txtCategoryName);
            TextView txtTaskCount = item.findViewById(R.id.txtTaskCount);

            txtName.setText(category.getName());
            txtTaskCount.setText(String.valueOf(category.getTaskCount()));
            item.setTag(category.getId());

            item.setOnLongClickListener(v -> {
                int categoryId = (int) v.getTag();
                showCategoryMenu(v, categoryId);
                return true;
            });

            gridCategory.addView(item);
        }
    }

    private void showEmptyCategory(boolean isEmpty) {
        gridCategory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        imgEmptyCategory.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        txtEmptyTitle.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        txtEmptyHint.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    /* ================= POPUP MENU ================= */
    private void showCategoryMenu(View anchor, int categoryId) {
        PopupMenu popupMenu = new PopupMenu(
                this,
                anchor,
                Gravity.END,
                0,
                R.style.CustomPopupMenu
        );

        popupMenu.getMenuInflater()
                .inflate(R.menu.menu_category, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                Toast.makeText(this,
                        "Edit category id = " + categoryId,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            if (item.getItemId() == R.id.action_delete) {
                Toast.makeText(this,
                        "Delete category id = " + categoryId,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        CategoryRequest request = new CategoryRequest(name.trim());

        categoryApi.createCategory(request)
                .enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(Call<CategoryResponse> call,
                                           Response<CategoryResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Đã tạo danh mục",
                                    Toast.LENGTH_SHORT
                            ).show();

                            edtAddTask.setText("");   // clear input
                            closeAddTask();           // đóng popup
                            loadCategories();         // reload danh mục
                        } else {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Tạo danh mục thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryResponse> call, Throwable t) {
                        Toast.makeText(
                                HomeActivity.this,
                                "Không kết nối được server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

}

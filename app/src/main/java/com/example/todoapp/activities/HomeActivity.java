package com.example.todoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton btnAddCategory;
    private EditText edtSearch, edtModalCategory;
    private CardView cardModal, cardSearch;
    private View overlay;

    private GridLayout gridCategory;
    private ImageView imgEmptyCategory, imgAvatar;
    private TextView txtEmptyTitle, txtEmptyHint, txtModalTitle, txtUserName, txtCategoryError;

    private Button btnSave, btnCancel;

    private SessionManager session;
    private CategoryApi categoryApi;

    private boolean isEdit = false;
    private int editingCategoryId = -1;

    // ===== SEARCH =====
    private List<CategoryResponse> allCategories = new ArrayList<>();
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* ================= ÁNH XẠ VIEW ================= */
        imgAvatar = findViewById(R.id.imgAvatar);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        cardModal = findViewById(R.id.cardModal);
        cardSearch = findViewById(R.id.cardSearch);
        edtSearch = findViewById(R.id.edtSearch);
        edtModalCategory = findViewById(R.id.edtModalCategory);
        overlay = findViewById(R.id.overlay);

        gridCategory = findViewById(R.id.gridCategory);
        imgEmptyCategory = findViewById(R.id.imgEmptyCategory);
        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);
        txtEmptyHint = findViewById(R.id.txtEmptyHint);
        txtModalTitle = findViewById(R.id.txtModalTitle);
        txtUserName = findViewById(R.id.txtUserName);
        txtCategoryError = findViewById(R.id.txtCategoryError);

        /* ================= INIT ================= */
        session = new SessionManager(this);
        categoryApi = ApiClient.getClient(this).create(CategoryApi.class);

        txtUserName.setText(session.getFullName());
        loadUser();
        loadCategories();

        /* ================= EVENT ================= */
        btnAddCategory.setOnClickListener(v -> {
            toggleOpenModal();
            txtModalTitle.setText("Thêm Danh Mục");
        });

        btnSave.setOnClickListener(v -> {
            submitCategory(edtModalCategory.getText().toString());
        });

        btnCancel.setOnClickListener(v -> resetModal());

        overlay.setOnClickListener(v -> closeModal());

        imgAvatar.setOnClickListener(v ->
                startActivity(new Intent(this, Settings.class))
        );

        setupSearchDebounce();
    }

    /* ================= USER ================= */
    private void loadUser() {
        String avatarUrl = session.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }

    /* ================= SEARCH (300ms) ================= */
    private void setupSearchDebounce() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> filterCategories(s.toString());
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCategories(String keyword) {
        if (allCategories == null) return;

        if (keyword == null || keyword.trim().isEmpty()) {
            loadCategoryToGrid(allCategories);
            return;
        }

        String key = keyword.toLowerCase().trim();
        List<CategoryResponse> filtered = new ArrayList<>();

        for (CategoryResponse c : allCategories) {
            if (c.getName().toLowerCase().contains(key)) {
                filtered.add(c);
            }
        }

        loadCategoryToGrid(filtered);
    }

    /* ================= MODAL ================= */
    private void toggleOpenModal() {
        cardModal.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        lockSearch(true);
    }

    private void closeModal() {
        cardModal.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        lockSearch(false);
    }

    private void resetModal() {
        closeModal();
        edtModalCategory.setText("");
        edtModalCategory.clearFocus();
        txtCategoryError.setVisibility(View.GONE);
        edtModalCategory.setBackgroundResource(R.drawable.bg_input);
        isEdit = false;
        editingCategoryId = -1;
    }

    private void lockSearch(boolean lock) {
        cardSearch.setEnabled(!lock);
        edtSearch.setEnabled(!lock);
        cardSearch.setAlpha(lock ? 0.4f : 1f);
    }

    /* ================= LOAD CATEGORY ================= */
    private void loadCategories() {
        if (!session.isLoggedIn()) {
            showEmptyCategory(true);
            return;
        }

        categoryApi.getMyCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call,
                                   Response<List<CategoryResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    allCategories = response.body(); // 👈 quan trọng
                    loadCategoryToGrid(allCategories);
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

    /* ================= GRID ================= */
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
            CardView cardView = item.findViewById(R.id.cardCategory);

            txtName.setText(category.getName());
            txtTaskCount.setText(String.valueOf(category.getTaskCount()));
            item.setTag(category.getId());

            int colorIndex = Math.abs(category.getId()) % CATEGORY_COLORS.length;
            cardView.setCardBackgroundColor(Color.parseColor(CATEGORY_COLORS[colorIndex]));
            txtName.setTextColor(Color.WHITE);

            item.setOnLongClickListener(v -> {
                showCategoryMenu(v, (int) v.getTag());
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

    /* ================= MENU ================= */
    private void showCategoryMenu(View anchor, int categoryId) {
        PopupMenu popupMenu = new PopupMenu(this, anchor, Gravity.END, 0, R.style.CustomPopupMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_category, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                isEdit = true;
                editingCategoryId = categoryId;

                TextView txtName = anchor.findViewById(R.id.txtCategoryName);
                edtModalCategory.setText(txtName.getText());
                toggleOpenModal();
                txtModalTitle.setText("Sửa Danh Mục");
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    /* ================= CREATE / UPDATE ================= */
    private void submitCategory(String name) {
        if (name == null || name.trim().isEmpty()) return;

        CategoryRequest request = new CategoryRequest(name.trim());
        Call<CategoryResponse> call = isEdit
                ? categoryApi.updateCategory(editingCategoryId, request)
                : categoryApi.createCategory(request);

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call,
                                   Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    resetModal();
                    loadCategories();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this,
                        "Không kết nối được server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ================= COLORS ================= */
    private static final String[] CATEGORY_COLORS = {
            "#3B82F6","#22C55E","#F59E0B","#EF4444","#8B5CF6",
            "#06B6D4","#F97316","#EC4899","#10B981","#6366F1"
    };
}

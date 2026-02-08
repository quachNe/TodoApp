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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.view.ViewGroup;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
                    allCategories = response.body();
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
            CardView cardView = item.findViewById(R.id.cardCategory);

            txtName.setText(
                    category.getName() + " (" + category.getTaskCount() + ")"
            );
            item.setTag(category.getId());

            int colorIndex = Math.abs(category.getId()) % CATEGORY_COLORS.length;
            cardView.setCardBackgroundColor(Color.parseColor(CATEGORY_COLORS[colorIndex]));
            txtName.setTextColor(Color.WHITE);

            ImageView btnMore = item.findViewById(R.id.btnMenu);

            cardView.setOnClickListener(v -> {
                startActivity(new Intent(this, CategoryDetailActivity.class));
            });
            btnMore.setOnClickListener(v -> {
                showCategoryMenu(item, category.getId());
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
        View view = getLayoutInflater()
                .inflate(R.layout.popup_category_menu, null);

        PopupWindow popup = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popup.setOutsideTouchable(true);
        popup.setElevation(10f);

        TextView btnEdit = view.findViewById(R.id.btnEdit);
        TextView btnDelete = view.findViewById(R.id.btnDelete);

        btnEdit.setOnClickListener(v -> {
            isEdit = true;
            editingCategoryId = categoryId;

            TextView txtName = anchor.findViewById(R.id.txtCategoryName);
            // chỉ lấy tên, bỏ "(0)"
            String fullText = txtName.getText().toString();
            String nameOnly = fullText.replaceAll("\\s*\\(.*\\)", "");
            edtModalCategory.setText(nameOnly);

            toggleOpenModal();
            txtModalTitle.setText("Sửa Danh Mục");
            popup.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            popup.dismiss();
            showDeleteConfirm(categoryId);
        });

        // hiện menu gọn sát góc phải
        popup.showAsDropDown(anchor, -270, -310, Gravity.END);
    }


    /* ================= CREATE / UPDATE ================= */
    private void submitCategory(String name) {
        TextInputEditText txtModalTitle = findViewById(R.id.edtModalCategory);
        TextView txtCategoryError = findViewById(R.id.txtCategoryError);

        if (name == null || name.trim().isEmpty()) {
            txtModalTitle.requestFocus();
            return;
        }

        CategoryRequest request = new CategoryRequest(name.trim());
        Call<CategoryResponse> call = isEdit
                ? categoryApi.updateCategory(editingCategoryId, request)
                : categoryApi.createCategory(request);

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call,
                                   Response<CategoryResponse> response) {
                if (response.code() == 409) {
                    txtCategoryError.setText("Tên danh mục đã tồn tại!");
                    txtCategoryError.setVisibility(View.VISIBLE);
                    return;
                }
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
            "#22D3EE", // Cyan sáng
            "#34D399", // Emerald sáng
            "#FBBF24", // Amber sáng
            "#FB7185", // Rose nổi
            "#A78BFA", // Violet sáng
            "#60A5FA", // Blue sáng
            "#F97316", // Orange nổi
            "#F472B6", // Pink neon
            "#2DD4BF", // Teal sáng
            "#818CF8"  // Indigo sáng
    };

    private void deleteCategory(int categoryId) {
        categoryApi.deleteCategory(categoryId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Đã xóa danh mục",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadCategories(); // reload grid
                        } else {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Không thể xóa danh mục",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(
                                HomeActivity.this,
                                "Lỗi kết nối server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showDeleteConfirm(int categoryId) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa danh mục?")
                .setMessage("Danh mục này sẽ bị xóa vĩnh viễn. Bạn có chắc không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteCategory(categoryId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

package com.example.todoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

    private FloatingActionButton btnAddCategory;
    private EditText edtSearch, edtModalCategory;
    private CardView cardModal, cardSearch;
    private View overlay;

    private GridLayout gridCategory;
    private ImageView imgEmptyCategory;
    private TextView txtEmptyTitle, txtEmptyHint, txtModalTitle, txtDate;

    private SessionManager session;
    private CategoryApi categoryApi;

    private Button btnSave, btnCancel;

    private boolean isEdit = false;
    private int editingCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* ================= ÁNH XẠ VIEW ================= */
        imgAvatar = findViewById(R.id.imgAvatar);
        btnMenu = findViewById(R.id.btnMenu);
        txtDate = findViewById(R.id.txtDate);

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

        /* ================= INIT ================= */
        session = new SessionManager(this);
        categoryApi = ApiClient.getClient(this).create(CategoryApi.class);

        setCurrentDate();
        loadUser();
        loadCategories();

        btnAddCategory.setOnClickListener(v -> {
            toggleOpenModal();
            txtModalTitle.setText("Thêm Danh Mục");
        } );

        btnMenu.setOnClickListener(v ->
                startActivity(new Intent(this, Settings.class))
        );

        btnSave.setOnClickListener(v -> {
            String name = edtModalCategory.getText().toString();
            submitCategory(name);
        });

        overlay.setOnClickListener(v -> closeModal());
        btnCancel.setOnClickListener(v -> closeModal());
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

    /* ================= Modal ================= */
    private void toggleOpenModal() {
        if (cardModal.getVisibility() == View.GONE) {
            cardModal.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            lockSearch(true);
        } else {
            closeModal();
        }
    }

    private void closeModal() {
        cardModal.setVisibility(View.GONE);
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
            CardView cardView = item.findViewById(R.id.cardCategory);
            // 👆 id CardView trong item_category.xml

            txtName.setText(category.getName());
            txtTaskCount.setText(String.valueOf(category.getTaskCount()));
            item.setTag(category.getId());

            // ===== 🎨 SET MÀU CATEGORY =====
            int colorIndex = Math.abs(category.getId()) % CATEGORY_COLORS.length;
            String colorHex = CATEGORY_COLORS[colorIndex];

            cardView.setCardBackgroundColor(Color.parseColor(colorHex));
            txtName.setTextColor(Color.WHITE);

            // ===== LONG CLICK =====
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
                isEdit = true;
                editingCategoryId = categoryId;

                TextView txtName = anchor.findViewById(R.id.txtCategoryName);
                edtModalCategory.setText(txtName.getText().toString());

                toggleOpenModal();
                edtModalCategory.requestFocus();
                txtModalTitle.setText("Sửa Danh Mục");
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

    private void submitCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            edtModalCategory.requestFocus();
            return;
        }

        CategoryRequest request = new CategoryRequest(name.trim());

        Call<CategoryResponse> call;

        // ====== XỬ LÝ THÊM / SỬA ======
        if (isEdit) {
            call = categoryApi.updateCategory(editingCategoryId, request);
        } else {
            call = categoryApi.createCategory(request);
        }

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call,
                                   Response<CategoryResponse> response) {

                if (response.isSuccessful()) {
                    edtModalCategory.setText("");
                    isEdit = false;
                    editingCategoryId = -1;

                    closeModal();
                    loadCategories();
                } else {
                    Toast.makeText(
                            HomeActivity.this,
                            isEdit ? "Cập nhật danh mục thất bại" : "Tạo danh mục thất bại",
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

    private static final String[] CATEGORY_COLORS = {
            "#3B82F6", // Blue
            "#22C55E", // Green
            "#F59E0B", // Amber
            "#EF4444", // Red
            "#8B5CF6", // Purple
            "#06B6D4", // Cyan
            "#F97316", // Orange
            "#EC4899", // Pink
            "#10B981", // Emerald
            "#6366F1", // Indigo

            "#84CC16", // Lime
            "#14B8A6", // Teal
            "#A855F7", // Violet
            "#E11D48", // Rose
            "#0EA5E9", // Sky
            "#F43F5E", // Rose Red
            "#22D3EE", // Light Cyan
            "#4ADE80", // Light Green
            "#FB7185", // Soft Red
            "#C084FC", // Light Purple

            "#FACC15", // Yellow
            "#FDBA74", // Peach
            "#67E8F9", // Ice Blue
            "#A7F3D0", // Mint
            "#FCA5A5", // Light Red
            "#E879F9", // Magenta
            "#93C5FD", // Light Blue
            "#D9F99D", // Light Lime
            "#FDE68A", // Light Yellow
            "#FBCFE8", // Light Pink

            "#64748B", // Slate
            "#475569", // Dark Slate
            "#1E293B", // Navy Dark
            "#334155", // Blue Gray
            "#78716C", // Stone
            "#A8A29E", // Neutral
            "#0F766E", // Deep Teal
            "#166534", // Deep Green
            "#7C2D12", // Brown
            "#713F12", // Dark Gold

            "#365314", // Olive
            "#3F6212", // Olive Dark
            "#4C1D95", // Deep Purple
            "#881337", // Deep Pink
            "#7F1D1D", // Deep Red
            "#1F2937", // Dark Gray
            "#020617", // Almost Black
            "#312E81", // Indigo Dark
            "#155E75", // Blue Teal
            "#064E3B"  // Dark Emerald
    };


}

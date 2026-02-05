package com.example.todoapp.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.todoapp.R;
import com.example.todoapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.widget.PopupMenu;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView txtDate;

    private FloatingActionButton btnAddCategory;

    private EditText edtSearch;
    private View cardAddTask;
    private View overlay;

    private CardView cardCategory, cardSearch; // demo 1 category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* ================= ÁNH XẠ VIEW ================= */
        imgAvatar = findViewById(R.id.imgAvatar);
        txtDate = findViewById(R.id.txtDate);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        cardAddTask = findViewById(R.id.cardAddTask);
        overlay = findViewById(R.id.overlay);

        cardCategory = findViewById(R.id.cardCategory);
        cardSearch = findViewById(R.id.cardSearch);
        edtSearch = findViewById(R.id.edtSearch);


        /* ================= SET NGÀY ================= */
        setCurrentDate();

        /* ================= LOAD AVATAR ================= */
        loadUser();

        /* ================= FAB ADD TASK ================= */
        btnAddCategory.setOnClickListener(v -> toggleAddTask());

        /* ================= LONG PRESS CATEGORY ================= */
        cardCategory.setOnLongClickListener(v -> {
            showCategoryMenu(v);
            return true;
        });

        overlay.setOnClickListener(v -> {
            cardAddTask.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            cardSearch.setEnabled(true);
            edtSearch.setEnabled(true);
            edtSearch.setFocusable(true);
            edtSearch.setFocusableInTouchMode(true);
            cardSearch.setAlpha(1f);
        });
    }

    /* ======================== METHODS ======================== */

    private void setCurrentDate() {
        Date now = new Date();
        Locale localeVN = new Locale("vi", "VN");
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE, d 'Tháng' M yyyy", localeVN);

        String currentDate = sdf.format(now);
        currentDate =
                currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);

        txtDate.setText(currentDate);
    }

    private void loadUser() {
        SessionManager session = new SessionManager(this);

        // Load avatar
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

        // Nếu sau này có text tên
        // txtName.setText(session.getFullName());
    }


    private void toggleAddTask() {
        if (cardAddTask.getVisibility() == View.GONE) {
            cardAddTask.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);

            // KHÓA SEARCH
            cardSearch.setEnabled(false);
            edtSearch.setEnabled(false);
            edtSearch.setFocusable(false);
            edtSearch.setFocusableInTouchMode(false);
            cardSearch.setAlpha(0.4f); // mờ cho có cảm giác bị khóa

        } else {
            cardAddTask.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);

            // MỞ LẠI SEARCH
            cardSearch.setEnabled(true);
            edtSearch.setEnabled(true);
            edtSearch.setFocusable(true);
            edtSearch.setFocusableInTouchMode(true);
            cardSearch.setAlpha(1f);
        }
    }


    /* ================= POPUP MENU CATEGORY ================= */
    private void showCategoryMenu(View anchor) {
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
            int id = item.getItemId();

            if (id == R.id.action_edit) {
                Toast.makeText(this,
                        "Chỉnh sửa category", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (id == R.id.action_delete) {
                Toast.makeText(this,
                        "Xóa category", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }
}
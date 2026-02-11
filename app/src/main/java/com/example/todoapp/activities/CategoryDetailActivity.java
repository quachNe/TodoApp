package com.example.todoapp.activities;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

public class CategoryDetailActivity extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout layoutTaskContainer;
    private View openedItem = null;

    private TextView categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        categoryTitle = findViewById(R.id.categoryTitle);

        String categoryName = getIntent().getStringExtra("categoryName");
        categoryTitle.setText(categoryName);

        btnBack = findViewById(R.id.btnBack);
        layoutTaskContainer = findViewById(R.id.layoutTaskContainer);

        btnBack.setOnClickListener(v -> finish());

        addTaskItem();
        addTaskItem();
        addTaskItem();
    }

    private void addTaskItem() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_task, layoutTaskContainer, false);
        layoutTaskContainer.addView(itemView);

        View card = itemView.findViewById(R.id.cardContent);
        ImageView btnEdit = itemView.findViewById(R.id.btnEdit);
        ImageView btnDelete = itemView.findViewById(R.id.btnDelete);

        btnEdit.setOnClickListener(v ->
                Toast.makeText(this, "Sửa task", Toast.LENGTH_SHORT).show()
        );

        btnDelete.setOnClickListener(v -> {
            layoutTaskContainer.removeView(itemView);
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
        });

        enableSwipe(card);
    }

    private void enableSwipe(@NonNull View card) {

        final float MAX_SWIPE = -280f;      // trượt sang trái
        final float OPEN_THRESHOLD = -100f;  // quá nửa thì mở

        GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {

                        if (Math.abs(distanceX) > Math.abs(distanceY)) {

                            float newTranslation = card.getTranslationX() - distanceX;

                            // Chỉ cho trượt sang trái
                            if (newTranslation > 0)
                                newTranslation = 0;

                            if (newTranslation < MAX_SWIPE)
                                newTranslation = MAX_SWIPE;

                            card.setTranslationX(newTranslation);
                            return true;
                        }

                        return false;
                    }
                });

        card.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }

            detector.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {

                if (card.getTranslationX() < OPEN_THRESHOLD) {
                    card.animate().translationX(MAX_SWIPE).setDuration(200);
                    openedItem = card;
                } else {
                    card.animate().translationX(0).setDuration(200);
                    openedItem = null;
                }

                v.getParent().requestDisallowInterceptTouchEvent(false);
            }

            return true;
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (openedItem != null) {

            if (ev.getAction() == MotionEvent.ACTION_DOWN) {

                int[] location = new int[2];
                openedItem.getLocationOnScreen(location);

                float x = ev.getRawX();
                float y = ev.getRawY();

                if (!(x > location[0] &&
                        x < location[0] + openedItem.getWidth() &&
                        y > location[1] &&
                        y < location[1] + openedItem.getHeight())) {

                    // Click ra ngoài -> đóng lại
                    openedItem.animate().translationX(0).setDuration(200);
                    openedItem = null;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

}

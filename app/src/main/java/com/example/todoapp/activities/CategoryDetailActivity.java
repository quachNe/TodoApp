package com.example.todoapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiClient;
import com.example.todoapp.api.TaskApi;
import com.example.todoapp.models.Task;
import com.example.todoapp.requests.TaskRequest;
import com.example.todoapp.responses.TaskResponse;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {
    private List<Task> allTasks;
    private String currentFilter = "TOTAL"; // TOTAL | TODAY | COMPLETED | PENDING
    private String searchKeyword = "";

    private ImageView btnBack;
    private LinearLayout layoutTaskContainer, layoutEmpty;
    private View openedItem = null;

    private View overlay;
    private TextView edtSearch, categoryTitle, tvTotal, tvToday, tvCompleted, tvPending, txtPickDate, txtPickTime,txtTaskError;

    private MaterialCardView cardTotal, cardToday, cardCompleted, cardPending;
    private CardView cardModal;
    private FloatingActionButton btnAddTask;
    private Button btnCancel, btnSaveTask;
    private TextInputEditText edtTaskName;
    private int categoryId;
    private String selectedDate = null;
    private String selectedTime = null;

    private boolean isEdit = false;
    private int editingTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // ============================ Ánh xạ ============================
        categoryTitle = findViewById(R.id.categoryTitle);
        btnBack = findViewById(R.id.btnBack);
        layoutTaskContainer = findViewById(R.id.layoutTaskContainer);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        tvTotal = findViewById(R.id.tvTotal);
        tvToday = findViewById(R.id.tvToday);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvPending = findViewById(R.id.tvPending);

        cardTotal = findViewById(R.id.cardTotal);
        cardToday = findViewById(R.id.cardToday);
        cardCompleted = findViewById(R.id.cardCompleted);
        cardPending = findViewById(R.id.cardPending);
        btnAddTask = findViewById(R.id.btnAddTask);
        overlay = findViewById(R.id.overlay);
        cardModal = findViewById(R.id.cardModal);
        txtPickDate = findViewById(R.id.txtPickDate);
        txtPickTime = findViewById(R.id.txtPickTime);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        edtTaskName = findViewById(R.id.edtTaskName);
        txtTaskError = findViewById(R.id.txtTaskError);
        edtSearch = findViewById(R.id.edtSearch);

        // ============================ Lấy dữ liệu ============================
        categoryId = getIntent().getIntExtra("categoryId", -1);
        String categoryName = getIntent().getStringExtra("categoryName");
        categoryTitle.setText(categoryName);
        loadTasksFromApi();

        // ============================ Xử lý sự kiện ============================
        cardTotal.setOnClickListener(v -> {
            currentFilter = "TOTAL";
            applyFilter();
            setActive(cardTotal, tvTotal);
            setNoActive(cardToday, tvToday);
            setNoActive(cardCompleted, tvCompleted);
            setNoActive(cardPending, tvPending);
        });

        cardToday.setOnClickListener(v -> {
            currentFilter = "TODAY";
            applyFilter();
            setActive(cardToday, tvToday);
            setNoActive(cardTotal, tvTotal);
            setNoActive(cardCompleted, tvCompleted);
            setNoActive(cardPending, tvPending);
        });

        cardCompleted.setOnClickListener(v -> {
            currentFilter = "COMPLETED";
            applyFilter();
            setActive(cardCompleted, tvCompleted);
            setNoActive(cardTotal, tvTotal);
            setNoActive(cardToday, tvToday);
            setNoActive(cardPending, tvPending);
        });

        cardPending.setOnClickListener(v -> {
            currentFilter = "PENDING";
            applyFilter();
            setActive(cardPending, tvPending);
            setNoActive(cardTotal, tvTotal);
            setNoActive(cardToday, tvToday);
            setNoActive(cardCompleted, tvCompleted);
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                searchKeyword = s.toString();
                applyFilter();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        btnAddTask.setOnClickListener(v -> {
                cardModal.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
        });

        txtPickDate.setOnClickListener(v -> showDatePicker());
        txtPickTime.setOnClickListener(v -> showTimePicker());

        btnCancel.setOnClickListener(v -> {
            cardModal.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            resetForm();
        });

        overlay.setOnClickListener(v -> {
            cardModal.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            resetForm();
        });
        btnBack.setOnClickListener(v -> finish());
        btnSaveTask.setOnClickListener(v -> {
            handleSubit();
        });
    }
    private void resetForm(){
        txtPickDate.setText("Chọn ngày");
        txtPickTime.setText("Chọn giờ");
        selectedDate = null;
        selectedTime = null;
        edtTaskName.setText("");
        cardModal.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        txtTaskError.setVisibility(View.GONE);
    }
    private void handleSubit(){

        String name = edtTaskName.getText().toString().trim();

        if (name.isEmpty()){
            edtTaskName.requestFocus();
            return;
        }

        if (selectedDate == null) {
            txtTaskError.setVisibility(View.VISIBLE);
            txtTaskError.setText("Vui lòng chọn ngày");
            return;
        }

        if (selectedTime == null) {
            txtTaskError.setVisibility(View.VISIBLE);
            txtTaskError.setText("Vui lòng chọn giờ");
            return;
        }

        try {

            // =============================
            // 1️⃣ Ghép ngày giờ
            // =============================
            String dateTimeString = selectedDate + " " + selectedTime;

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(dateTimeString);

            Date now = new Date();

            if (date.before(now)) {
                txtTaskError.setVisibility(View.VISIBLE);
                txtTaskError.setText("Không được chọn thời gian trong quá khứ");
                return;
            }

            // =============================
            // 2️⃣ Convert sang ISO gửi BE
            // =============================
            SimpleDateFormat isoFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            String deadlineIso = isoFormat.format(date);

            txtTaskError.setVisibility(View.GONE);

            // =============================
            // 3️⃣ Gọi API tạo Task
            // =============================
            TaskApi taskApi = ApiClient.getClient(this).create(TaskApi.class);

            TaskRequest request =
                    new TaskRequest(name, deadlineIso);

            Call<TaskResponse> call = isEdit ? taskApi.updateTask(editingTaskId, request):
                    taskApi.createTask(categoryId, request);

            call.enqueue(new Callback<TaskResponse>() {

                @Override
                public void onResponse(Call<TaskResponse> call,
                                       Response<TaskResponse> response) {

                    if (response.code() == 409) {
                        txtTaskError.setText("Task đã tồn tại!");
                        txtTaskError.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (response.isSuccessful()) {
                        isEdit = false;
                        editingTaskId = -1;

                        resetForm();
                        loadTasksFromApi();
                    }
                }

                @Override
                public void onFailure(Call<TaskResponse> call, Throwable t) {
                    Toast.makeText(CategoryDetailActivity.this,
                            "Không kết nối được server", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {

                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    selectedTime = time;
                    txtPickTime.setText(time);
                },
                hour,
                minute,
                true // true = 24h format
        );

        timePickerDialog.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDate = date;
                    txtPickDate.setText(date);

                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker()
                .setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void setActive(MaterialCardView card, TextView tv) {
        card.setCardBackgroundColor(Color.parseColor("#6366F1"));
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(null, Typeface.BOLD);
    }

    private void setNoActive(MaterialCardView card, TextView tv) {
        card.setCardBackgroundColor(Color.parseColor("#F3F4F6"));
        tv.setTextColor(Color.parseColor("#374151"));
        tv.setTypeface(null, Typeface.NORMAL);
    }

    // ===============================
    // LOAD TASKS FROM API
    // ===============================
    private void loadTasksFromApi() {

        TaskApi api = ApiClient.getClient(this).create(TaskApi.class);

        Call<TaskResponse> call = api.getTasksByCategory(categoryId);

        call.enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call,
                                   Response<TaskResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    allTasks = response.body().getTasks();

                    TaskResponse.Summary summary = response.body().getSummary();
                    tvTotal.setText("Tổng (" + summary.getTotal() + ")");
                    tvToday.setText("Hôm nay (" + summary.getToday() + ")");
                    tvCompleted.setText("Đã hoàn thành (" + summary.getCompleted() + ")");
                    tvPending.setText("Chưa hoàn thành (" + summary.getPending() + ")");

                    applyFilter();

                } else {
                    Toast.makeText(CategoryDetailActivity.this,
                            "Load thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                Toast.makeText(CategoryDetailActivity.this,
                        "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {

        layoutTaskContainer.removeAllViews();

        if (allTasks == null || allTasks.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            return;
        }

        layoutEmpty.setVisibility(View.GONE);

        Date now = new Date();

        SimpleDateFormat isoFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateOnly =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Task task : allTasks) {

            boolean show = false;

            try {

                Date deadline = null;

                if (task.getDeadline() != null) {
                    deadline = isoFormat.parse(task.getDeadline());
                }

                switch (currentFilter) {

                    case "TOTAL":
                        show = true;
                        break;

                    case "TODAY":
                        if (deadline != null &&
                                dateOnly.format(deadline)
                                        .equals(dateOnly.format(now))) {
                            show = true;
                        }
                        break;

                    case "COMPLETED":
                        show = task.isCompleted();
                        break;

                    case "PENDING":
                        show = !task.isCompleted();
                        break;
                }

                // SEARCH
                if (!task.getTaskName()
                        .toLowerCase()
                        .contains(searchKeyword.toLowerCase())) {
                    show = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (show) {
                addTaskItem(task);
            }
        }

        if (layoutTaskContainer.getChildCount() == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    // ===============================
    // ADD TASK ITEM UI
    // ===============================
    private boolean isTaskExpired(Task task) {
        if (task.getDeadline() == null) return false;

        try {
            SimpleDateFormat isoFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date deadlineDate = isoFormat.parse(task.getDeadline());
            Date now = new Date();

            return deadlineDate.before(now);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addTaskItem(Task task){
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_task, layoutTaskContainer, false);
        layoutTaskContainer.addView(itemView);
        TextView tvTaskTime = itemView.findViewById(R.id.txtTaskTime);
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {

            try {
                SimpleDateFormat isoFormat =
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                Date date = isoFormat.parse(task.getDeadline());

                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

                String formatted = dateFormat.format(date);

                tvTaskTime.setText(formatted);

            } catch (Exception e) {
                e.printStackTrace();
                tvTaskTime.setText("Không xác định");
            }

        } else {
            tvTaskTime.setText("Không có deadline");
        }

        View card = itemView.findViewById(R.id.cardContent);
        ImageView btnEdit = itemView.findViewById(R.id.btnEdit);
        ImageView btnDelete = itemView.findViewById(R.id.btnDelete);
        TextView tvTaskName = itemView.findViewById(R.id.txtTaskTitle);

        tvTaskName.setText(task.getTaskName());

        btnEdit.setOnClickListener(v -> {
            txtTaskError.setVisibility(View.GONE);
            isEdit = true;
            editingTaskId = task.getId();

            cardModal.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);

            edtTaskName.setText(task.getTaskName());

            if (task.getDeadline() != null) {

                try {
                    // Parse ISO string
                    SimpleDateFormat isoFormat =
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                    Date date = isoFormat.parse(task.getDeadline());

                    // Format date
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    SimpleDateFormat timeFormat =
                            new SimpleDateFormat("HH:mm", Locale.getDefault());

                    String formattedDate = dateFormat.format(date);
                    String formattedTime = timeFormat.format(date);

                    txtPickDate.setText(formattedDate);
                    txtPickTime.setText(formattedTime);

                    selectedDate = formattedDate;
                    selectedTime = formattedTime;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(v -> {
            deleteTask(task.getId(), itemView);
        });
        enableSwipe(card);
        CheckBox cbCompleted = itemView.findViewById(R.id.cbCompleted);
        if (isTaskExpired(task)) {

            // Làm mờ card một chút
            card.setAlpha(0.6f);

            // Không cho tick
            cbCompleted.setEnabled(false);

            // Không cho chỉnh sửa
            btnEdit.setVisibility(View.GONE);

            // Không cho swipe (nếu swipe dùng cho edit)
            card.setOnTouchListener((v, event) -> true);

            // VẪN cho delete
            btnDelete.setEnabled(true);
        }
    }

    // ===============================
    // DELETE TASK API
    // ===============================
    private void deleteTask(int taskId, View itemView) {

        TaskApi api = ApiClient.getClient(this).create(TaskApi.class);

        api.deleteTask(taskId).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call,
                                   Response<TaskResponse> response) {

                if (response.isSuccessful()) {
                    layoutTaskContainer.removeView(itemView);
                    Toast.makeText(CategoryDetailActivity.this,
                            "Đã xóa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                Toast.makeText(CategoryDetailActivity.this,
                        "Lỗi xóa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===============================
    // SWIPE
    // ===============================
    private void enableSwipe(@NonNull View card) {

        final float MAX_SWIPE = -280f;
        final float OPEN_THRESHOLD = -100f;

        GestureDetector detector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {

                        if (Math.abs(distanceX) > Math.abs(distanceY)) {

                            float newTranslation = card.getTranslationX() - distanceX;

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

                    if (openedItem != null && openedItem != card) {
                        openedItem.animate().translationX(0).setDuration(200);
                    }

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

                    openedItem.animate().translationX(0).setDuration(200);
                    openedItem = null;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}

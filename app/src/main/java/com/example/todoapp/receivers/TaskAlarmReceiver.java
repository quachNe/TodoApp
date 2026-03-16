package com.example.todoapp.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.activities.LoginActivity;
import com.example.todoapp.utils.NotificationHelper;
import com.example.todoapp.utils.SessionManager;

public class TaskAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String taskName = intent.getStringExtra("taskName");
        String reminderType = intent.getStringExtra("reminderType");
        String accountName = intent.getStringExtra("accountName");
        String categoryName = intent.getStringExtra("categoryName");


        if (reminderType == null) reminderType = "deadline";

        android.util.Log.d("TaskAlarmReceiver",
                "onReceive: " + taskName + " - " + reminderType);

        String title;
        String text;
        boolean vibrate = false;
        Uri soundUri = null;

        switch (reminderType) {

            case "1_day":
                title = "Nhắc nhở: Task sắp đến hạn";
                text = taskName + " - " + categoryName + " (Còn 1 ngày) | " + accountName;
                soundUri = RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION);
                break;

            case "1_hour":
                title = "Nhắc nhở: Task sắp đến hạn";
                text = taskName + " - " + categoryName + " (Còn 1 giờ) | " + accountName;
                vibrate = true;
                break;

            case "deadline":
            default:
                title = "Task đến hạn";
                text = taskName + " - " + categoryName + " | " + accountName;
                vibrate = true;
                soundUri = RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION);
                break;
        }

        // =============================
        // Intent mở app khi click notification
        // =============================
        SessionManager sessionManager = new SessionManager(context);

        Intent openAppIntent;

        if (sessionManager.isLoggedIn()) {

            // Session còn hạn → vào Main
            openAppIntent = new Intent(context, MainActivity.class);

        } else {

            // Token hết hạn → vào Login
            openAppIntent = new Intent(context, LoginActivity.class);
        }

        openAppIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        // =============================
        // Tạo notification
        // =============================
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        NotificationHelper.CHANNEL_ID
                )
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        if (soundUri != null) {
            builder.setSound(soundUri);
        }

        if (vibrate) {
            builder.setVibrate(new long[]{0, 500, 500, 500});
        }

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(context);

        // =============================
        // Kiểm tra permission Android 13+
        // =============================
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // =============================
        // Rung thiết bị
        // =============================
        if (vibrate) {

            Vibrator vibrator =
                    (Vibrator) context.getSystemService(
                            Context.VIBRATOR_SERVICE);

            if (vibrator != null && vibrator.hasVibrator()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    VibrationEffect effect =
                            VibrationEffect.createWaveform(
                                    new long[]{0, 500, 500, 500},
                                    -1
                            );

                    vibrator.vibrate(effect);

                } else {

                    vibrator.vibrate(
                            new long[]{0, 500, 500, 500},
                            -1
                    );
                }
            }
        }

        // =============================
        // Hiện notification
        // =============================
        manager.notify(
                (int) System.currentTimeMillis(),
                builder.build()
        );
    }
}
package com.example.todoapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.todoapp.R;
import com.example.todoapp.utils.NotificationHelper;

public class TaskAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String taskName = intent.getStringExtra("taskName");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // nên dùng icon launcher
                        .setContentTitle("Task đến hạn")
                        .setContentText(taskName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(context);

        // kiểm tra quyền Android 13+
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
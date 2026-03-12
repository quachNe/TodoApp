package com.example.todoapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.todoapp.receivers.TaskAlarmReceiver;

public class TaskScheduler {

    public static void scheduleTask(Context context,
                                    int taskId,
                                    long deadlineMillis,
                                    String taskName) {

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.putExtra("taskName", taskName);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        taskId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (alarmManager.canScheduleExactAlarms()) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        deadlineMillis,
                        pendingIntent
                );

            } else {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        deadlineMillis,
                        pendingIntent
                );
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    deadlineMillis,
                    pendingIntent
            );

        } else {

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    deadlineMillis,
                    pendingIntent
            );
        }
    }
}
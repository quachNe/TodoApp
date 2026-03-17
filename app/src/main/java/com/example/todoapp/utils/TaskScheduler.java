package com.example.todoapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.todoapp.receivers.TaskAlarmReceiver;

public class TaskScheduler {

    public static void scheduleTask(Context context,
                                    int taskId,
                                    long deadlineMillis,
                                    String taskName,
                                    String accountName,
                                    String categoryName) {

        // Cancel existing reminders first to avoid duplicates
        cancelReminders(context, taskId);

        // Schedule reminder 1 day before
//        scheduleReminder(
//                context,
//                taskId,
//                deadlineMillis - 24 * 60 * 60 * 1000,
//                taskName,
//                accountName,
//                categoryName,
//                "1_day"
//        );

        // Schedule reminder 1 hour before
//        scheduleReminder(
//                context,
//                taskId,
//                deadlineMillis - 60 * 60 * 1000,
//                taskName,
//                accountName,
//                categoryName,
//                "1_hour"
//        );

        // Schedule at deadline
//        scheduleReminder(
//                context,
//                taskId,
//                deadlineMillis,
//                taskName,
//                accountName,
//                categoryName,
//                "deadline"
//        );

        // Nhắc trước 5 phút
        scheduleReminder(
                context,
                taskId,
                deadlineMillis - 5 * 60 * 1000,
                taskName,
                accountName,
                categoryName,
                "1_day");


        //TEST: nhắc trước 2 phút
        scheduleReminder(
                context,
                taskId,
                deadlineMillis - 2 * 60 * 1000,
                taskName,
                accountName,
                categoryName,
                "1_hour");

        //TEST: đúng deadline
        scheduleReminder(
                context,
                taskId,
                deadlineMillis,
                taskName,
                accountName,
                categoryName,
                "deadline");
    }

    private static void scheduleReminder(Context context,
                                         int taskId,
                                         long triggerMillis,
                                         String taskName,
                                         String accountName,
                                         String categoryName,
                                         String reminderType) {

        if (triggerMillis <= System.currentTimeMillis()) {
            Log.d("TaskScheduler", "Bỏ qua " + reminderType + " cho task " + taskId);
            return;
        }

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.putExtra("taskName", taskName);
        intent.putExtra("accountName", accountName);
        intent.putExtra("reminderType", reminderType);
        intent.putExtra("categoryName", categoryName);

        int requestCode = taskId * 10 + getTypeCode(reminderType);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
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
                        triggerMillis,
                        pendingIntent
                );

            } else {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                );
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
            );

        } else {

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
            );
        }

        Log.d("TaskScheduler",
                "Scheduled " + reminderType + " for task " + taskId);
    }

    private static int getTypeCode(String reminderType) {
        switch (reminderType) {
            case "1_day": return 2;
            case "1_hour": return 1;
            case "deadline": return 0;
            default: return 0;
        }
    }

    public static void cancelReminders(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        for (int typeCode = 0; typeCode <= 2; typeCode++) {
            int requestCode = taskId * 10 + typeCode;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }
    }
}
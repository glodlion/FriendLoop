package com.example.friendloop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BirthdayNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm", "BirthdayNotificationReceiver 被觸發，開始執行提醒邏輯");

        // 執行提醒邏輯，例如發送通知
        BirthdayNotification(context);
    }

    private void BirthdayNotification(Context context) {
        // 一次性執行任務（啟動時立即執行）
        OneTimeWorkRequest immediateWorkRequest = new OneTimeWorkRequest.Builder(BirthdayNotificationManager.class).build();
        WorkManager.getInstance(context.getApplicationContext()).enqueue(immediateWorkRequest);

        // 定期執行任務（每天檢查一次）
        PeriodicWorkRequest birthdayWorkRequest = new PeriodicWorkRequest.Builder(
                BirthdayNotificationManager.class,
                1, // 每1天執行一次
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        "BirthdayReminderWork",
                        ExistingPeriodicWorkPolicy.KEEP,
                        birthdayWorkRequest
                );
    }
}
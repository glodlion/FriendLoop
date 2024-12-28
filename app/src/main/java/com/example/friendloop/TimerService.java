package com.example.friendloop;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;

public class TimerService extends Service implements Runnable{
//    public static final String TAG = MyTimeWidget.TAG;
    public static final String CLICK_EVENT = "android.appwidget.action.Click";
    private String strPromotionUri = "http://jumpin.cc";
    private com.example.friendloop.NotificationHelper mNotiHelper;
    private static final int NOTIFICATION_SECONDARY1 = 1200;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("Timer", "handler");
            if (msg.what == 1){
                update();
                Log.d("Timer", "what");
            }
        }
    };

    @Override
    public void run() {
        handler.sendEmptyMessage(1);
        handler.postDelayed(this,60*1000);
        Log.d("Timer", "run");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotiHelper = new NotificationHelper(getApplicationContext());
        startForegroundNotification(); // 添加此方法
        Log.d("Timer", "onCreate:(Service)");
        handler.sendEmptyMessage(1);
        handler.post(this);
    }

    /**更新時間*/
    private void update(){
        Log.d("Timer", "1");
        birthdaycheck();
    }

    private void birthdaycheck() {
        Log.d("BirthdayNotification", "doWork() 被執行");
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        Cursor cursor = null;

        try {
            cursor = dbHelper.getTodayBirthdays();
            if (cursor != null && cursor.moveToFirst()) {
                Log.d("BirthdayNotification", "找到生日資料");
                do {
                    int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                    int birthdayIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY);

                    if (nameIndex != -1 && birthdayIndex != -1) {
                        String friendName = cursor.getString(nameIndex);
                        String birthday = cursor.getString(birthdayIndex);
                        Log.d("BirthdayNotification", "朋友: " + friendName + "，生日: " + birthday);
                        sendBirthdayNotification(friendName);
                    }
                } while (cursor.moveToNext());
            }else{
                Log.d("BirthdayNotification", "今天沒有朋友生日");
            }
        } catch (Exception e) {
            Log.e("BirthdayNotification", "Error checking birthdays", e);
        } finally {
            if (cursor != null) cursor.close();
        }

    }

    public void sendBirthdayNotification(String friendName)
    {
        Log.d("BirthdayNotification", "發送通知給: " + friendName);
        String title = "生日提醒";
        String body = friendName + " 今天生日！快去祝福吧！";
        String imageUri = "https://example.com/birthday_image"; // 可以更換成您的圖片

        Notification nb = mNotiHelper.getNotification2(title, body, strPromotionUri);
        if (nb != null)
        {
            mNotiHelper.notify(NOTIFICATION_SECONDARY1, nb);
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TimerServiceChannel",
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startForegroundNotification() {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "TimerServiceChannel")
                .setContentTitle("Timer Service")
                .setContentText("Service is running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
    }
}

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        handler.postDelayed(this,24*60*60*1000);
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
        Cursor cursor = dbHelper.getAllContacts();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Log.d("BirthdayNotification","開始判斷");
                    int BirthdayIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY);
                    String BirthDay = BirthdayIndex < 0 ? "Default Value" : cursor.getString(BirthdayIndex);
                    int NameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                    String Name = NameIndex < 0 ? "Default Value" : cursor.getString(NameIndex);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // 將生日轉換為 Date 對象
                    Date date = dateFormat.parse(BirthDay);

                    // 提取月份和日期
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

                    String month = monthFormat.format(date); // 提取月份
                    String day = dayFormat.format(date);     // 提取日期
                    Log.d("BirthdayNotification","生日是"+month);
                    Log.d("BirthdayNotification","生日是"+day);
                    // 獲取當下的月份和日期
                    Calendar today = Calendar.getInstance();
                    String currentMonth = today.get(Calendar.MONTH) + 1 +""; // 月份需要加 1，因為 Calendar.MONTH 從 0 開始
                    String currentDay = today.get(Calendar.DAY_OF_MONTH)+"";
                    Log.d("BirthdayNotification","現在是"+currentMonth);
                    Log.d("BirthdayNotification","現在是"+currentDay);
                    if(month.equals(currentMonth))
                    {
                        if(currentDay.equals(day)){
                            Log.d("BirthdayNotification","進去了1");
                            sendBirthdayNotification(Name);
                        }
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("BirthdayNotification", "Error checking birthdays", e);
        } finally {
            if (cursor != null) cursor.close();
        }

    }

    public void sendBirthdayNotification(String friendName)
    {
        if (mNotiHelper == null) {
            mNotiHelper = new NotificationHelper(getApplicationContext());
        }

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
        //Notification notification = new NotificationCompat.Builder(this, "TimerServiceChannel")
         //       .setContentTitle("Timer Service")
         //       .setContentText("Service is running...")
         //       .setSmallIcon(R.drawable.ic_launcher_foreground)
          //      .build();
      //  startForeground(1, notification);
    }
}

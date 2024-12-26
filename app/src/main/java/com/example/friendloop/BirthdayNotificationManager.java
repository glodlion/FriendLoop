package com.example.friendloop;
import android.app.Notification;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class BirthdayNotificationManager extends  Worker{

    private String strPromotionUri = "http://jumpin.cc";
    private com.example.friendloop.NotificationHelper mNotiHelper;
    private static final int NOTIFICATION_SECONDARY1 = 1200;

    public BirthdayNotificationManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mNotiHelper = new NotificationHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
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

        return Result.success();
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
}

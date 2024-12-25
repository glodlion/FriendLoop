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
    }

    @NonNull
    @Override
    public Result doWork() {
        // 獲取今天的日期
        String today = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date());
        // 初始化資料庫助手
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());


        // 查詢今天生日的朋友
        Cursor cursor = null;
        try {
            cursor = dbHelper.getTodayBirthdays();

            // 如果有結果，遍歷資料並發送通知
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 檢查欄位索引是否正確
                    int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                    int birthdayIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY);

                    // 如果欄位索引有效
                    if (nameIndex != -1 && birthdayIndex != -1) {
                        // 獲取朋友的名字和生日
                        String friendName = cursor.getString(nameIndex);
                        String birthday = cursor.getString(birthdayIndex);

                        // 發送通知
                        sendBirthdayNotification(friendName, birthday);
                    } else {
                        Log.e("BirthdayWorker", "欄位索引無效，請檢查資料表結構和查詢語句！");
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("BirthdayWorker", "今天沒有朋友生日！");
            }
        } catch (Exception e) {
            Log.e("BirthdayWorker", "查詢資料庫時發生錯誤：", e);
        } finally {
            // 確保游標被關閉
            if (cursor != null) {
                cursor.close();
            }
        }

        return Result.success();
    }
    public void sendBirthdayNotification(String friendName, String birthday)
    {

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

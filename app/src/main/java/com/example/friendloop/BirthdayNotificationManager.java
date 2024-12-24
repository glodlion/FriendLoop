//package com.example.friendloop;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//import java.util.Date;
//
//public class BirthdayNotificationManager extends  Worker{
//
//    public BirthdayNotificationManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        // 1. 獲取今天的日期（格式 MM-dd）
//        String today = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date());
//
//        // 2. 使用 SqlDataBaseHelper 查詢是否有朋友生日
//        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
//        Cursor cursor = dbHelper.getTodayBirthdays(today);
//
//        // 3. 如果有生日的朋友，發送通知
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String friendName = cursor.getString(cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME));
//                sendBirthdayNotification(friendName);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        // 4. 返回成功狀態
//        return Result.success();
//    }
//}

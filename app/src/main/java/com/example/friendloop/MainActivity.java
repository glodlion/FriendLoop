package com.example.friendloop;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity{
    RecyclerView mFriendRecyclerView;
    protected HippoCustomRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Friend> mDataset = new ArrayList<>();
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected SqlDataBaseHelper mSqlDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 檢查權限
        if (!checkPermissions()) {
            requestPermissions();
        }

        mSqlDataBaseHelper = new SqlDataBaseHelper(this);
//        mSqlDataBaseHelper.resetTable(); //當資料表變動時執行一次
        initSharedPreferences();
        initFloatingActionButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("alarm", "onDestroy 被調用，設置 1 分鐘後的提醒");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // 設置觸發時間為當前時間的 1 分鐘後
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0); // 設置小時為 0 (12:00 AM)
            calendar.set(Calendar.MINUTE, 0);     // 設置分鐘為 0
            calendar.set(Calendar.SECOND, 0); // 秒設置為 0
            calendar.set(Calendar.MILLISECOND, 0); // 毫秒設置為 0
            // 如果當前時間已經過了今天的 0:00，設置為明天的 0:00
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1); // 日期加 1 天
            }

            long triggerTime = calendar.getTimeInMillis();
            Log.d("alarm", "AlarmManager 設置的觸發時間：" + new Date(triggerTime).toString());

            // 創建 PendingIntent
            Intent intent = new Intent(this, BirthdayNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 設置 AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );

            Log.d("alarm", "AlarmManager 已設置，將在 1 分鐘後觸發");
        }
    }
    private void createNotificationChannel() {
        // 僅在 Android 8.0（API 26）及以上版本需要創建通知通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default"; // 與通知中指定的 channelId 一致
            String channelName = "Birthday Notification";
            String channelDescription = "提醒朋友的生日通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            // 註冊通知通道
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
//    private void setupDailyAlarm() {
//        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//        boolean isAlarmSet = prefs.getBoolean("isAlarmSet", false);
//        BirthdayNotification(this);
//        if (!isAlarmSet) {
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, 9);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//
//            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
//                calendar.add(Calendar.DAY_OF_YEAR, 1);
//            }
//            Intent intent = new Intent(this, BirthdayNotificationReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//            if (alarmManager != null) {
//                alarmManager.setRepeating(
//                        AlarmManager.RTC_WAKEUP,
//                        calendar.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY,
//                        pendingIntent
//                );
//            }
//            // 更新 SharedPreferences，標記 Alarm 已設置
//            prefs.edit().putBoolean("isAlarmSet", true).apply();
//        }
//    }

    private void BirthdayNotification(Context context) {
        // 一次性執行任務（啟動時立即執行）
        OneTimeWorkRequest immediateWorkRequest = new OneTimeWorkRequest.Builder(BirthdayNotificationManager.class).build();
        WorkManager.getInstance(context.getApplicationContext()).enqueue(immediateWorkRequest);

        // 定期執行任務（每天檢查一次）
        PeriodicWorkRequest birthdayWorkRequest = new PeriodicWorkRequest.Builder(
                BirthdayNotificationManager.class,
                1, // 每1天執行一次
                TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(
                        "BirthdayReminderWork",
                        ExistingPeriodicWorkPolicy.KEEP,
                        birthdayWorkRequest
                );
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
        initRecycleView();
    }

    private enum LayoutManagerType
    {
        LINEAR_LAYOUT_MANAGER
    }

    private boolean checkPermissions() {
        // 檢查相機和儲存權限
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // 請求相機和儲存權限
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 300) {
            // 處理使用者的授權回應
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限已授予
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                // 權限被拒絕
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 初始化SharedPreferences，用來存個人資訊及編輯狀態(個人資訊、朋友資訊)
    private void initSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("state")){
            editor.putString("name", "姓名");
            editor.putString("phone", "電話");
            editor.putString("birthday", "生日");
            editor.putString("preference", "備註");
            editor.putString("picture", "android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_foreground);
        }
        editor.putInt("state", 0); // 狀態值： 1 表示個人資訊頁面，2 表示朋友資訊頁面
        editor.apply();
    }

    public void initRecyclerView() {
        mDataset = new ArrayList<>();
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this);
        Cursor cursor = dbHelper.getAllContacts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Friend friend = new Friend();
                int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                int pictureIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_IMAGE_URI);
                String name = nameIndex < 0 ? "Default Value" : cursor.getString(nameIndex);
                String picture = pictureIndex < 0 ? "Default Value" : cursor.getString(pictureIndex);
                friend.setName(name);
                friend.setPicture(picture);

                //friend.setPhone(cursor.getString(cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PHONE)));
                //friend.setBirthday(cursor.getString(cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY)));
                // 假設每個好友有一個固定圖片
                //friend.setPicture(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_android_));
                mDataset.add(friend);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }



    public void initRecycleView()
    {
        mFriendRecyclerView = (RecyclerView) findViewById(R.id.friendRecyclerView);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        //  TO DO 動態載入自定義之 HippoCustomRecyclerViewAdapter 物件mAdapter，包含自訂UI friend_list_item.xml。
        mAdapter = new HippoCustomRecyclerViewAdapter(MainActivity.this, mDataset);
        mFriendRecyclerView.setAdapter(mAdapter);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType)
    {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mFriendRecyclerView.getLayoutManager() != null)
        {
            scrollPosition = ((LinearLayoutManager) mFriendRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }


        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mFriendRecyclerView.setLayoutManager(mLayoutManager);
        mFriendRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initFloatingActionButton(){
        FloatingActionButton mAddFriendFab = findViewById(R.id.addFriendFab);
        mAddFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "add friend", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    // 接收回傳資料
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 100) {
            // 取得資料


        }
    }

    public void onPersonalClick(View view) {
        // 修改 SharedPreferences 中的 state 值
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("state", 1); // 修改狀態為 1，表示進入個人頁面
        editor.apply(); // 提交變更（異步儲存）

        Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
        startActivity(intent);
    }

    public void onQrClick(View view) {
        Intent intent = new Intent(MainActivity.this, QrActivity.class);
        startActivity(intent);
    }

//    public void onDotClick(View view) {
//        Toast.makeText(this, "點擊了更多", Toast.LENGTH_LONG).show();
//    }
}
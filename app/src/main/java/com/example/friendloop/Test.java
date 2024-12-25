package com.example.friendloop;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.annotation.NonNull;

public class Test extends AppCompatActivity {

    public static boolean bIfDebug = false;
    public static String TAG = "HIPPO_DEBUG";
    private Button btn1,btn2;
    private static final int NOTIFICATION_PRIMARY1 = 1100;
    private static final int NOTIFICATION_SECONDARY1 = 1200;
    private com.example.friendloop.NotificationHelper mNotiHelper;
    private String strPromotionUri = "http://jumpin.cc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            init();
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init()
    {
        mNotiHelper = new com.example.friendloop.NotificationHelper(this);
        //btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
//        btn1.setOnClickListener(new Button.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                sendNotification(NOTIFICATION_PRIMARY1, getTitlePrimaryText());
//            }
//        });

        btn2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 測試發送通知功能
                String friendName = "測試用例";
                String birthday = "2024-12-24";

                // 使用 NotificationHelper 測試發送通知
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                Notification notification = notificationHelper.getNotification2(
                        "生日提醒",
                        friendName + " 今天生日！快去祝福吧！",
                        null // 這裡可以傳入圖片 URI，如果沒有可以設為 null
                );

                if (notification != null) {
                    notificationHelper.notify(1, notification);
                }

                // 啟動 Worker 來偵測是否有人生日
                OneTimeWorkRequest testWorkRequest = new OneTimeWorkRequest.Builder(BirthdayNotificationManager.class)
                        .build();

                WorkManager.getInstance(getApplicationContext()).enqueue(testWorkRequest);
            }
        });

    }

    public void sendNotification(int id, String title)
    {
        Notification nb = null;
        switch (id)
        {
//            case NOTIFICATION_PRIMARY1:
//                nb = mNotiHelper.getNotification1(title, getString(R.string.str_notification_body_1));
//                break;
            case NOTIFICATION_SECONDARY1:
                nb = mNotiHelper.getNotification2(title, getString(R.string.str_notification_body_2), strPromotionUri);
                break;
        }
        if (nb != null)
        {
            mNotiHelper.notify(id, nb);
        }
    }

//    private String getTitlePrimaryText()
//    {
//        if (btn1 != null)
//        {
//            return btn1.getText().toString();
//        }
//        return "";
//    }

    private String getTitleSecondaryText()
    {
        if (btn2 != null)
        {
            return btn2.getText().toString();
        }
        return "";
    }

    public void goToNotificationSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    public void goToNotificationSettings(String channel)
    {
        //  TO DO
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID,channel);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1,Menu.FIRST, Menu.FIRST,getString(R.string.menu_primary_setting));
        menu.add(1,Menu.FIRST+1, Menu.FIRST+1,getString(R.string.menu_secondary_setting));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case Menu.FIRST:
                goToNotificationSettings(com.example.friendloop.NotificationHelper.PRIMARY_CHANNEL);
                break;
            case Menu.FIRST+1:
                goToNotificationSettings(com.example.friendloop.NotificationHelper.SECONDARY_CHANNEL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
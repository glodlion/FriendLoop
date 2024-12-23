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
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn1.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendNotification(NOTIFICATION_PRIMARY1, getTitlePrimaryText());
            }
        });

        btn2.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendNotification(NOTIFICATION_SECONDARY1, getTitleSecondaryText());
            }
        });
    }

    public void sendNotification(int id, String title)
    {
        Notification nb = null;
        switch (id)
        {
            case NOTIFICATION_PRIMARY1:
                nb = mNotiHelper.getNotification1(title, getString(R.string.str_notification_body_1));
                break;
            case NOTIFICATION_SECONDARY1:
                nb = mNotiHelper.getNotification2(title, getString(R.string.str_notification_body_2), strPromotionUri);
                break;
        }
        if (nb != null)
        {
            mNotiHelper.notify(id, nb);
        }
    }

    private String getTitlePrimaryText()
    {
        if (btn1 != null)
        {
            return btn1.getText().toString();
        }
        return "";
    }

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
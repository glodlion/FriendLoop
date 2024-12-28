package com.example.friendloop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper
{

    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    public NotificationHelper(Context ctx)
    {
        super(ctx);
        NotificationChannel channelB = null;
        channelB = new NotificationChannel(SECONDARY_CHANNEL,getString(R.string.str_secondary_notification),
                NotificationManager.IMPORTANCE_DEFAULT);
        getManager().createNotificationChannel(channelB);
    }
    public Notification getNotification2(String title, String body, String strUri)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_NOTIFICATION_MSG,body);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded_);
        String curtime = DateUtils.formatDateTime(this,System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        expandedView.setTextViewText(R.id.timestamp,curtime);
        expandedView.setTextViewText(R.id.notification_message,body);
        Intent leftIntent = new Intent(this, NotificationDismissReceiver.class);
        leftIntent.setAction("ACTION_DISMISS_NOTIFICATION"); // 自定義動作
        PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(this, 100, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        expandedView.setOnClickPendingIntent(R.id.notification_collapsed_left_button, pendingIntentLeft);
        Intent rightIntent = new Intent(this, MainActivity.class);
        rightIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 確保是主畫面
        PendingIntent pendingIntentRight = PendingIntent.getActivity(this, 200, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        expandedView.setOnClickPendingIntent(R.id.notification_collapsed_right_button, pendingIntentRight);
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed_);
        expandedView.setTextViewText(R.id.content_text,title);
        expandedView.setTextViewText(R.id.timestamp,curtime);
        expandedView.setImageViewResource(R.id.notification_collapsed_left_button, R.drawable.no);
        expandedView.setImageViewResource(R.id.notification_collapsed_right_button, R.drawable.yes);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setSmallIcon(R.drawable.sms)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setAutoCancel(true)
                .build();
        return notification;
    }

    public void notify(int id, Notification notification)
    {
        getManager().notify(id, notification);
    }

    private NotificationManager getManager()
    {
        if (manager == null)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}

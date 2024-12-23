package com.example.friendloop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.friendloop.Constants;
import com.example.friendloop.R;
import com.example.friendloop.my_msg_handler;
import android.net.Uri;

public class NotificationHelper extends ContextWrapper
{
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    public NotificationHelper(Context ctx)
    {
        super(ctx);

        NotificationChannel channelA = null;
        // TO DO
        channelA = new NotificationChannel(PRIMARY_CHANNEL,getString(R.string.str_primary_notification),
                NotificationManager.IMPORTANCE_DEFAULT);
        getManager().createNotificationChannel(channelA);

        NotificationChannel channelB = null;
        // TO DO
        channelB = new NotificationChannel(SECONDARY_CHANNEL,getString(R.string.str_secondary_notification),
                NotificationManager.IMPORTANCE_DEFAULT);
        getManager().createNotificationChannel(channelB);
    }

    public Notification getNotification1(String title, String body)
    {
        Intent intent = new Intent(this, my_msg_handler.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // TO DO
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_NOTIFICATION_MSG,body);
        intent.putExtras(bundle);

        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.notificationbg);

        Notification notification = null;
        // TO DO
        notification = new NotificationCompat.Builder(getApplicationContext(),PRIMARY_CHANNEL)
                .setSmallIcon(R.drawable.sms)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(icon)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                .setContentIntent(pendingintent)
                .setAutoCancel(true)
                .build();

        return notification;
    }

    public Notification getNotification2(String title, String body, String strUri)
    {
        Intent intent = new Intent(this, my_msg_handler.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // TO DO
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_NOTIFICATION_MSG,body);
        intent.putExtras(bundle);

        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // TO DO
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded_);
        // TO DO
        String curtime = DateUtils.formatDateTime(this,System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME);
        expandedView.setTextViewText(R.id.timestamp,curtime);
        expandedView.setTextViewText(R.id.notification_message,body);

        Intent leftIntent = new Intent(this, my_msg_handler.class);
        leftIntent.putExtras(bundle);
        leftIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 100, leftIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        expandedView.setOnClickPendingIntent(R.id.notification_collapsed_left_button, pendingIntent1);


        Intent rightIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
        rightIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 200, rightIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        expandedView.setOnClickPendingIntent(R.id.notification_collapsed_right_button, pendingIntent2);


        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed_);
        // TO DO
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
                .setContentIntent(pendingintent)
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

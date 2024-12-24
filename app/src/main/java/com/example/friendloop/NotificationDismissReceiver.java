package com.example.friendloop;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("ACTION_DISMISS_NOTIFICATION".equals(intent.getAction())) {
            // 確保通知被關閉
            Log.d("NotificationDismissReceiver", "Notification dismissed");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancelAll(); // 或指定通知 ID，例如 manager.cancel(1);
        }
    }
}

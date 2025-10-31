package in.megasoft.workplace;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = null;
        String body = null;
        Map<String, String> data = remoteMessage.getData();
        Log.d("FCM_DEBUG", "Data payload: " + new JSONObject(data).toString());

        // 1. Prefer Data payload
        if (remoteMessage.getData().size() > 0) {

            // Map<String, String> data = remoteMessage.getData();

            // Convert to JSON-like string for logging
            Log.d("FCM_DEBUG", "Data payload: " + new JSONObject(data).toString());

            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");

            Log.d("FCM_DEBUG", "MainActivity: from_notification=true, title=" + title + ", body=" + body);
        }

        // 2. Fallback to Notification payload
        if (title == null && remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if (title == null) title = "No Title";
        if (body == null) body = "No Body";

        Log.d("FCM_DEBUG", "MainActivity: from_notification=true, title=" + title + ", body=" + body);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "default", "General Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Send broadcast so NotificationActivity can refresh
        Intent broadcastIntent = new Intent("NEW_NOTIFICATION");
        broadcastIntent.putExtra("from_notification", true);
        broadcastIntent.putExtra("notif_title", title);
        broadcastIntent.putExtra("notif_body", body);
        sendBroadcast(broadcastIntent);

        // Build PendingIntent for LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("from_notification", true);
        intent.putExtra("notif_title", title);
        intent.putExtra("notif_body", body);

        // This is important to ensure the intent is delivered as-is:
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

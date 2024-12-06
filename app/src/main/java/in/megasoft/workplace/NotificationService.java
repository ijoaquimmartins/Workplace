package in.megasoft.workplace;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "foreground_service_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the service in the foreground
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notification Service")
                .setContentText("Running in the background...")
                .setSmallIcon(R.drawable.ic_notification) // Add your app's notification icon
                .build();

        startForeground(1, notification);

        // Perform your task (e.g., scheduling periodic work)
        schedulePeriodicTask();

        // Return START_STICKY to keep the service alive
        return START_STICKY;
    }

    private void schedulePeriodicTask() {
        // Example: Scheduling WorkManager
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 5, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build())
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup tasks if needed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Not binding to any activity
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}


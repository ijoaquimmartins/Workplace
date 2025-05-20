package in.megasoft.workplace;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class update {
    public static void checkForUpdate(Context context, Runnable onNoUpdate) {
        new Thread(() -> {
            try {

                URL url = new URL("https://mssgpsdata.in/megasoft/public/app/update.json");
            //  URL url = new URL("http:/100.168.10.75/workplace/public/app/update.json");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                int latestVersion = jsonObject.getInt("versionCode");
                String apkUrl = jsonObject.getString("apkUrl");

                int currentVersion = BuildConfig.VERSION_CODE;

                if (latestVersion > currentVersion) {
                    ((Activity) context).runOnUiThread(() -> showUpdateDialog(context, apkUrl));
                } else {
                    ((Activity) context).runOnUiThread(onNoUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(onNoUpdate);
            }
        }).start();
    }
    private static void showUpdateDialog(Context context, String apkUrl) {
        new AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version is available. Do you want to update?")
            .setPositiveButton("Yes", (dialog, which) -> downloadAndInstallApk(context, apkUrl))
            .setNegativeButton("Later", null)
            .show();
    }
    private static void downloadAndInstallApk(Context context, String apkUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Downloading Update");
        request.setDescription("Please wait...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "mss.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Uri apkUri = manager.getUriForDownloadedFile(downloadId);
                installApk(context, apkUri);
                context.unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.registerReceiver(onComplete, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            ContextCompat.registerReceiver(context, onComplete, filter, ContextCompat.RECEIVER_EXPORTED);
        }
//        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    private static void installApk(Context context, Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use FileProvider if needed
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}

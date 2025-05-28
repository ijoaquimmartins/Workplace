package in.megasoft.workplace;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;

public class Update {
    public static void checkForUpdate(Activity activity, Runnable onNoUpdate) {
        String url = "https://mssgpsdata.in/megasoft/public/app/update.json";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int latestVersion = jsonObject.getInt("versionCode");
                    String apkUrl = jsonObject.getString("apkUrl");
                    int currentVersion = BuildConfig.VERSION_CODE;
                    if (latestVersion > currentVersion) {
                        showUpdateDialog(activity, apkUrl);
                    } else {
                        onNoUpdate.run();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onNoUpdate.run();
                }
            },
            error -> {
                error.printStackTrace();
                onNoUpdate.run();
            }
        );
        requestQueue.add(stringRequest);
    }
    private static void showUpdateDialog(Activity activity, String apkUrl) {
        new AlertDialog.Builder(activity)
            .setTitle("Update Available")
            .setMessage("A new version is available. Do you want to update?")
            .setPositiveButton("Yes", (dialog, which) -> downloadAndInstallApk(activity, apkUrl))
            .setNegativeButton("Later", null)
            .show();
    }
    private static void downloadAndInstallApk(Activity activity, String apkUrl) {
        File apkFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "mss.apk");
        if (apkFile.exists()) {
            boolean deleted = apkFile.delete();
            if (!deleted) {
                Toast.makeText(activity, "Failed to delete old APK", Toast.LENGTH_SHORT).show();
            }
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Downloading Update");
        request.setDescription("Please wait...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.fromFile(apkFile));
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                File apkFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "mss.apk");
                installApk(activity, apkFile);
                ctxt.unregisterReceiver(this);
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                activity,
                onComplete,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            );
        } else {
            activity.registerReceiver(onComplete, filter);
        }
    }
    private static void installApk(Activity activity, File apkFile) {
        Uri apkUri = FileProvider.getUriForFile(
            activity,
            activity.getPackageName() + ".fileprovider",
            apkFile
        );
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setData(apkUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to start installer", Toast.LENGTH_LONG).show();
        }
    }
}

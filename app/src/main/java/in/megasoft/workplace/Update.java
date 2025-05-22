package in.megasoft.workplace;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Update {
    public static void checkForUpdate(Context context, Runnable onNoUpdate) {
        String url = "https://mssgpsdata.in/megasoft/public/app/update.json";
        // String url = "http://100.168.10.75/workplace/public/app/update.json"; // for local testing

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int latestVersion = jsonObject.getInt("versionCode");
                        String apkUrl = jsonObject.getString("apkUrl");

                        int currentVersion = BuildConfig.VERSION_CODE;

                        if (latestVersion > currentVersion) {
                            showUpdateDialog(context, apkUrl);
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
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}

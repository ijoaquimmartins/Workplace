package in.megasoft.workplace;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;

public class Update {
    private static final int INSTALL_REQUEST_CODE = 1234;

    public static void checkForUpdate(Activity activity, Runnable onNoUpdate) {
        String url = "https://mssgpsdata.in/megasoft/public/app/update.json";
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int latestVersion = jsonObject.getInt("versionCode");
                        String apkUrl = jsonObject.getString("apkUrl");

                        int currentVersion = 0;
                        PackageManager pm = activity.getPackageManager();
                        PackageInfo pInfo = pm.getPackageInfo(activity.getPackageName(), 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            currentVersion = (int) pInfo.getLongVersionCode();
                        } else {
                            currentVersion = pInfo.versionCode;
                        }

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
        File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mss.apk");
        if (apkFile.exists()) apkFile.delete();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Downloading Update");
        request.setDescription("Please wait...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "mss.apk");

        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        AlertDialog progressDialog = new AlertDialog.Builder(activity)
                .setTitle("Downloading Update")
                .setMessage("Please wait...\n0%")
                .setCancelable(false)
                .create();
        progressDialog.show();

        new Thread(() -> {
            boolean downloading = true;
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
            while (downloading) {
                Cursor cursor = manager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                    if (bytesTotal > 0) {
                        final int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
                        activity.runOnUiThread(() -> progressDialog.setMessage("Please wait...\n" + progress + "%"));
                    }

                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                        downloading = false;
                    }
                    cursor.close();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            activity.runOnUiThread(() -> {
                progressDialog.dismiss();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mss.apk");
                installApk(activity, file);
            });
        }).start();
    }

    private static void installApk(Activity activity, File apkFile) {
        Uri apkUri = FileProvider.getUriForFile(
            activity,
            activity.getPackageName() + ".fileprovider",
            apkFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            activity.startActivityForResult(intent, INSTALL_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to start installer", Toast.LENGTH_LONG).show();
        }
    }

    public static void handleActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            // Try relaunching the app after update
            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
            if (launchIntent != null) {
                activity.startActivity(launchIntent);
                activity.finish(); // optional
            }
        }
    }
}

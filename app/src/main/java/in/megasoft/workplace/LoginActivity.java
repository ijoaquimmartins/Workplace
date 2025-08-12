package in.megasoft.workplace;

import static in.megasoft.workplace.Functions.setAppVersion;
import static in.megasoft.workplace.userDetails.PublicURL;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etusername, etuserpassword;
    private Button btnlogin, btncancel;
    private TextView tvregister, txterrormsg, tvUpdate;
    private CheckBox cbrememberme;
    public String username, password, mId, stMassage, token;

    public static final String SHARED_PREFS = "sharedprefs";
    public static final String USERID = "userid";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";

    private final String loginurl = PublicURL + "login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ Check if opened from notification
        checkNotificationExtras(getIntent());

        createNotificationChannel(this);

        etusername = findViewById(R.id.editUserText);
        etuserpassword = findViewById(R.id.editPasswordText);
        btnlogin = findViewById(R.id.btnLogin);
        btncancel = findViewById(R.id.btnCancle);
        tvregister = findViewById(R.id.tvRegister);
        cbrememberme = findViewById(R.id.checkboxRemember);
        tvUpdate = findViewById(R.id.tvUpdate);

        tvUpdate.setOnClickListener(view -> {
            Update.checkForUpdate(this, this::autologin);
        });

        setAppVersion(this, tvUpdate);
        autologin();

        // ✅ Auto update check if fields not empty
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (this.getPackageManager().canRequestPackageInstalls()) {
                if (!etusername.getText().toString().isEmpty() && !etuserpassword.getText().toString().isEmpty()) {
                    Update.checkForUpdate(this, this::login);
                }
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:" + this.getPackageName()));
                ((Activity) this).startActivityForResult(intent, 1234);
            }
        } else {
            if (!etusername.getText().toString().isEmpty() && !etuserpassword.getText().toString().isEmpty()) {
                Update.checkForUpdate(this, this::login);
            }
        }

        // ✅ Register Activity
        tvregister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });

        // ✅ Login Button
        btnlogin.setOnClickListener(view -> {
            if (cbrememberme.isChecked()) {
                savedata();
            }
            login();
        });

        // ✅ Get FCM Token
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                token = task.getResult();
                Log.d("FCM", "Token: " + token);
            });
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Update.handleActivityResult(this, requestCode, resultCode, data);
    }
    */

    private void checkNotificationExtras(Intent intent) {
        boolean fromNotification = intent.getBooleanExtra("from_notification", false);
        Log.d("FCM_DEBUG", "LoginActivity: from_notification=" + fromNotification);

        if (fromNotification) {
            String title = intent.getStringExtra("notif_title");
            String body = intent.getStringExtra("notif_body");
            Log.d("FCM_DEBUG", "LoginActivity: title=" + title + ", body=" + body);

            Toast.makeText(this, "Opened from Notification: " + title, Toast.LENGTH_LONG).show();

        }
    }

    public void login() {
        username = etusername.getText().toString().trim();
        password = etuserpassword.getText().toString().trim();
        userDetails.UserName = username;
        mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!username.isEmpty() && !password.isEmpty()) {
            in.megasoft.workplace.HttpsTrustManager.allowAllSSL();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                        intent.putExtra(USER_NAME, username);

                        // ✅ Forward notification extras if any
                        if (getIntent().getBooleanExtra("from_notification", false)) {
                            Log.d("FCM_DEBUG", "Forwarding extras from LoginActivity to LoadingActivity");
                            intent.putExtras(getIntent());
                        }

                        startActivity(intent);
                        finish();
                    } else {
                        stMassage = response;
                        showAlertDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("username", username);
                    data.put("password", password);
                    data.put("deviceid", mId);
                    data.put("token", token);
                    return data;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(LoginActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void savedata() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERID, etusername.getText().toString().trim());
        editor.putString(PASSWORD, etuserpassword.getText().toString().trim());
        editor.apply();
    }

    public void autologin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERID, "");
        password = sharedPreferences.getString(PASSWORD, "");
        etusername.setText(username);
        etuserpassword.setText(password);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if ("success".equals(stMassage)) {
                Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                intent.putExtra(USER_NAME, username);

                if (getIntent().getBooleanExtra("from_notification", false)) {
                    Log.d("FCM_DEBUG", "Forwarding extras from LoginActivity to LoadingActivity");
                    intent.putExtras(getIntent());
                }

                startActivity(intent);
                finish();
            } else {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        boolean fromNotification = intent.getBooleanExtra("from_notification", false);
        String notifTitle = intent.getStringExtra("notif_title");
        String notifBody = intent.getStringExtra("notif_body");

        Log.d("FCM_DEBUG", "onNewIntent received: from_notification=" + fromNotification +
                ", title=" + notifTitle + ", body=" + notifBody);
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            String description = "Channel for app notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    Update.checkForUpdate(this, this::autologin);
                } else {
                    Toast.makeText(this, "Install permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

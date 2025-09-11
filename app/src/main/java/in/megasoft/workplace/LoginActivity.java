package in.megasoft.workplace;

import static in.megasoft.workplace.Functions.setAppVersion;
import static in.megasoft.workplace.userDetails.PublicURL;

import android.app.Activity;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etusername, etuserpassword;
    private Button btnlogin, btncancel;
    private TextView tvregister, tvUpdate;
    private CheckBox cbrememberme;
    public String username, password, mId, stMassage, token;

    public static final String SHARED_PREFS = "sharedprefs";
    public static final String USERID = "userid";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";

    private final String loginurl = PublicURL + "login.php";

    /*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // very important!
        checkNotificationExtras(intent);
    }
*/
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("FCM", "onResume extras: " + getIntent().getExtras());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if opened from notification
        checkNotificationExtras(getIntent());

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

        // Try auto-login if prefs available
        autologin();

        // Update check if username & password filled
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

        // Register Activity
        tvregister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });

        // Login Button
        btnlogin.setOnClickListener(view -> {
            if (cbrememberme.isChecked()) {
                savedata();
            }

            if (token == null) {
                Toast.makeText(this, "Please wait, fetching token...", Toast.LENGTH_SHORT).show();
                FirebaseMessaging.getInstance().getToken()
                        .addOnSuccessListener(t -> {
                            token = t;
                            login(); // retry login once token is available
                        });
            } else {
                login();
            }
        });

        // Get FCM Token
    FirebaseMessaging.getInstance().getToken()
        .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();
            Log.d("FCM", "Token: " + token);

            // Save in SharedPreferences
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("fcm_token", token)
                .apply();
        });
    }

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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginurl,
                response -> {
                    if (response.equals("success")) {
                        Intent intent;
                        if (getIntent().getBooleanExtra("from_notification", false)) {
                            intent = new Intent(LoginActivity.this, NotificationActivity.class);
                            intent.putExtras(getIntent());
                        } else {
                            intent = new Intent(LoginActivity.this, LoadingActivity.class);
                            intent.putExtra(USER_NAME, username);
                        }
                        intent.putExtra(USER_NAME, username);
                        startActivity(intent);
                        finish();
                    } else {
                        stMassage = response;
                        showAlertDialog();
                    }
                },
                error -> Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show()
            ) {
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

        if (!username.isEmpty() && !password.isEmpty()) {
            // Already logged in → go to next activity
            Intent intent;
            if (getIntent().getBooleanExtra("from_notification", false)) {
                intent = new Intent(this, NotificationActivity.class);
                intent.putExtras(getIntent());
            } else {
                intent = new Intent(this, LoadingActivity.class);
                intent.putExtra(USER_NAME, username);
            }
            intent.putExtra(USER_NAME, username);
            startActivity(intent);
            finish();
        } else {
            // stay on login screen
            etusername.setText("");
            etuserpassword.setText("");
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

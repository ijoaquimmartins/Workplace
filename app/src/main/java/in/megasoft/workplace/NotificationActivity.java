package in.megasoft.workplace;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> notifications;

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshList(); //  always reload DB

            //  show dialog only for real-time pushes
            String title = intent.getStringExtra("notif_title");
            String body  = intent.getStringExtra("notif_body");

            if (title != null && body != null) {
                showDialog(title, body);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.lvNotification);

        notifications = new NotificationDAO(this).getAllNotificationsAsText();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(adapter);

        handleIntent(getIntent());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
    //    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(notificationReceiver, new IntentFilter("NEW_NOTIFICATION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(notificationReceiver);
    }

    private void handleIntent(Intent intent) {
        boolean fromNotification = intent.getBooleanExtra("from_notification", false);
        String title = intent.getStringExtra("notif_title");
        String body = intent.getStringExtra("notif_body");

        refreshList();

        if (fromNotification && title != null && body != null) {
            showDialog(title, body);
        }
    }

    private void refreshList() {
        notifications.clear();
        notifications.addAll(new NotificationDAO(this).getAllNotificationsAsText());
        adapter.notifyDataSetChanged();
    }

    private void showDialog(String title, String body) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("OK", null)
                .show();
    }


}

package in.megasoft.workplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

public class LoadingActivity extends AppCompatActivity {

    private Functions functions;
    private String user_name, modulelist ;

    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);

        requestQueue = volleySingelton.getmInstance(this).getRequestQueue();

        Intent i = this.getIntent();

        userDetails.UserName = i.getStringExtra(LoginActivity.USER_NAME).toString();

        user_name = i.getStringExtra(LoginActivity.USER_NAME).toString();

        functions = new Functions(this);
        functions.getUserData();
        functions.getusermoduledata();

        int delayMillis = 3000;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, delayMillis);
    }

}
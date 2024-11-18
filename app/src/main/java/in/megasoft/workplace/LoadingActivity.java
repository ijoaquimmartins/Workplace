package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoadingActivity extends AppCompatActivity {

    private String user_name;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;

    // Flags to track responses
    private boolean isUserDataLoaded = false;
    private boolean isModuleDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        requestQueue = volleySingelton.getmInstance(this).getRequestQueue();

        Intent i = this.getIntent();
        userDetails.UserName = i.getStringExtra(LoginActivity.USER_NAME);
        user_name = i.getStringExtra(LoginActivity.USER_NAME);

        getUserData();
        getusermoduledata();
    }

    public void getUserData() {
        String url = userDetails.PublicURL + "getuserdetails.php?user_name=" + user_name;
        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    UserId = jsonObject.getString("userid");
                    userDetails.UserName = jsonObject.getString("username");
                    userDetails.UserFullName = jsonObject.getString("name");
                    userDetails.EmailID = jsonObject.getString("emailid");
                    userDetails.MobileNo = jsonObject.getString("mobileno");
                    userDetails.PicPath = jsonObject.isNull("picpath") ? null : jsonObject.getString("picpath");
                    userDetails.AttnMarkedAs = jsonObject.getString("atnstatus");
                    userDetails.AttnDateTime = jsonObject.getString("atndate");

                    Toast.makeText(LoadingActivity.this, "Welcome " + userDetails.UserFullName, Toast.LENGTH_SHORT).show();

                    isUserDataLoaded = true; // Mark as loaded
                    checkIfAllDataLoaded();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoadingActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoadingActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    public void getusermoduledata() {
        String cntUrl = PublicURL + "fatchmodule.php?username=" + user_name;
        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, cntUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] data = response.split("#");

                userDetails.AttendanceMark = Arrays.asList(data).contains("AttendanceMark") ? "1" : "2";
                userDetails.LeaveApplication = Arrays.asList(data).contains("LeaveApplication") ? "1" : "2";
                userDetails.DailyWork = Arrays.asList(data).contains("DailyWork") ? "1" : "2";
                userDetails.EmployeeDetails = Arrays.asList(data).contains("EmployeeDetails") ? "1" : "2";
                userDetails.HolidayDetails = Arrays.asList(data).contains("HolidayDetails") ? "1" : "2";
                userDetails.TotalLeave = Arrays.asList(data).contains("TotalLeave") ? "1" : "2";
                userDetails.ApproveLeave = Arrays.asList(data).contains("ApproveLeave") ? "1" : "2";
                userDetails.AttendanceDetails = Arrays.asList(data).contains("AttendanceDetails") ? "1" : "2";
                userDetails.LeaveDetails = Arrays.asList(data).contains("LeaveDetails") ? "1" : "2";
                userDetails.DailyWorkDetails = Arrays.asList(data).contains("DailyWorkDetails") ? "1" : "2";

                isModuleDataLoaded = true; // Mark as loaded
                checkIfAllDataLoaded();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoadingActivity.this, "Error fetching modules: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private void checkIfAllDataLoaded() {
        // Proceed if both data requests have been completed successfully
        if (isUserDataLoaded && isModuleDataLoaded) {
            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
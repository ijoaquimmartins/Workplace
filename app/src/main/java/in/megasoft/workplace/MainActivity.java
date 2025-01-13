package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.URL;
import static in.megasoft.workplace.userDetails.UserId;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedprefs";
    Button attendance, applyleave, dailywork, employeedetails,
            holidaydetails,  totalleave, approveleave, attendancedetails, leavedetails, dailyworkdetails;
    TextView userfullname, emailid, mobileno, tvAttnLeaveList, badge_notification_1, tvUserId, marqueeText;
    EditText etOldPassword, etNewPassword, etConfirmPassword;
    String stErrorMassage;
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, menuIconWithText(this, R.drawable.ic_person, "PROFILE"));
        menu.add(0, 2, 2, menuIconWithText(this, R.drawable.ic_password, "CHANGE PASSWORD"));
        menu.add(0, 3, 3, menuIconWithText(this, R.drawable.ic_logout, "LOGOUT"));

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                return true;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view1 = inflater.inflate(R.layout.change_password, null);
                builder.setView(view1);

                tvUserId = view1.findViewById(R.id.txtUserID);
                etOldPassword = view1.findViewById(R.id.txtOldPassword);
                etNewPassword = view1.findViewById(R.id.txtNewPassword);
                etConfirmPassword = view1.findViewById(R.id.txtConfirmPassword);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChangePassword();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            case 3:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private CharSequence menuIconWithText(Context context, int drawableId, String title) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        SpannableString sb = new SpannableString("   " + title);
        if (drawable != null) {
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sb;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userdata();
        rights();
        getLeaveCount();
        long totalTime = 60000;
        long interval = 180000;
        try {
            schedulePeriodicWork();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Intent serviceIntent = new Intent(MainActivity.this, RSSPullService.class);
        startService(serviceIntent);

        new CountDownTimer(totalTime, interval) {
            public void onTick(long millisUntilFinished) {
                getAttendanceLeave();
            }

            public void onFinish() {
                getAttendanceLeave();
            }
        }.start();

        getAttendanceLeave();

        marqueeText = findViewById(R.id.marqueeText);

        LottieAnimationView lottieAnimationView = findViewById(R.id.fireworkAnimation);
        lottieAnimationView.playAnimation();
        //getMarquee();

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent attendance = new Intent(MainActivity.this, MarkAttendance.class);
                startActivity(attendance);
            }
        });
        applyleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent applyleave = new Intent(MainActivity.this, LeaveApplication.class);
                startActivity(applyleave);
            }
        });
        dailywork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dailywork = new Intent(MainActivity.this, DailyWork.class);
                startActivity(dailywork);
            }
        });
        approveleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent approveleave = new Intent(MainActivity.this, ApproveLeavesList.class);
                startActivity(approveleave);
            }
        });
        leavedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent leavedetails = new Intent(MainActivity.this, LeaveDetailsList.class);
                startActivity(leavedetails);
            }
        });
        totalleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent totalleave = new Intent(MainActivity.this, TotalLeave.class);
                startActivity(totalleave);
            }
        });
        dailyworkdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dailyworkdetails = new Intent(MainActivity.this, DailyWorkDetails.class);
                startActivity(dailyworkdetails);
            }
        });
        holidaydetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent holidaydetails = new Intent(MainActivity.this, HolidayList.class);
                startActivity(holidaydetails);
            }
        });
        attendancedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent attendancedetails = new Intent(MainActivity.this, AttendanceDetails.class);
                startActivity(attendancedetails);
            }
        });
        employeedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent employeedetails = new Intent(MainActivity.this, EmployeeDetails.class);
                startActivity(employeedetails);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        userdata();
        rights();
        getLeaveCount();
        getAttendanceLeave();
    }
    public void userdata(){
        userfullname = findViewById(R.id.txtUserFullName);
        emailid = findViewById(R.id.txtEmailID);
        mobileno = findViewById(R.id.txtMobile);
        userfullname.setText(userDetails.UserFullName);
        emailid.setText(userDetails.EmailID);
        mobileno.setText(userDetails.MobileNo);
    }
    public void rights(){

        attendance = findViewById(R.id.btnAttendance);
        applyleave = findViewById(R.id.btnApply);
        dailywork = findViewById(R.id.btnDailyWork);
        employeedetails = findViewById(R.id.btnEmployee);
        holidaydetails = findViewById(R.id.btnHoliday);
        totalleave = findViewById(R.id.btnLeaves);
        approveleave = findViewById(R.id.btnApprove);
        badge_notification_1 = findViewById(R.id.badge_notification_1);
        attendancedetails = findViewById(R.id.btnAttenDetails);
        leavedetails= findViewById(R.id.btnLeaveDetails);
        dailyworkdetails = findViewById(R.id.btnWorkDetails);

        if(userDetails.AttendanceMark.equals("1")){
            attendance.setVisibility(View.VISIBLE);
        }
        if (userDetails.LeaveApplication.equals("1")){
            applyleave.setVisibility(View.VISIBLE);
        }
        if (userDetails.DailyWork.equals("1")){
            dailywork.setVisibility(View.VISIBLE);
        }
        if (userDetails.EmployeeDetails.equals("1")){
            employeedetails.setVisibility(View.VISIBLE);
        }
        if (userDetails.HolidayDetails.equals("1")){
            holidaydetails.setVisibility(View.VISIBLE);
        }
        if (userDetails.TotalLeave.equals("1")){
            totalleave.setVisibility(View.VISIBLE);
        }
        if (userDetails.ApproveLeave.equals("1")){
            approveleave.setVisibility(View.VISIBLE);
            badge_notification_1.setVisibility(View.VISIBLE);
        }
        if (userDetails.AttendanceDetails.equals("1")){
            attendancedetails.setVisibility(View.VISIBLE);
        }
        if (userDetails.LeaveDetails.equals("1")){
            leavedetails.setVisibility(View.VISIBLE);
        }
        if (userDetails.DailyWorkDetails.equals("1")){
            dailyworkdetails.setVisibility(View.VISIBLE);
        }
    }
    public void getMarquee(){
        String urlFetch = PublicURL + "employeedetails.php?id=" + UserId;
        RequestQueue request = Volley.newRequestQueue(this);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlFetch, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.getString("").equals("birthday")) {

                        }else {
                            marqueeText.setText(jsonObject.getString("username"));
                            marqueeText.setSelected(true);
                            ObjectAnimator animator = ObjectAnimator.ofFloat(marqueeText, "translationX", 500f, -500f);
                            animator.setDuration(5000);
                            animator.setRepeatCount(ObjectAnimator.INFINITE);
                            animator.setRepeatMode(ObjectAnimator.RESTART);
                            animator.start();
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }
    public void getLeaveCount(){
        String url = PublicURL + "getappliedleave.php";
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] data = response.split("#");
                String badge_notification = data[0];
                badge_notification_1.setText(badge_notification);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }
    public void setListViewHeightBasedOnChildren(ListView lvEmployee) {
        ListAdapter listAdapter = lvEmployee.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int itemCount = listAdapter.getCount();

        for (int i = 0; i < itemCount; i++) {
            View listItem = listAdapter.getView(i, null, lvEmployee);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lvEmployee.getLayoutParams();
        params.height = totalHeight + (lvEmployee.getDividerHeight() * (itemCount - 1));
        lvEmployee.setLayoutParams(params);
        lvEmployee.requestLayout();
    }
    private void getAttendanceLeave(){
        tvAttnLeaveList = findViewById(R.id.tvAttnLeaveList);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        tvAttnLeaveList.setText("Attendance and Leave List for " + sdf.format(new Date()));

        String url = PublicURL + "getattendanceleave.php";
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String[] employee = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        employee[i] = jsonArray.getString(i);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, employee);
                    ListView lvEmployee = findViewById(R.id.lvEmployeeList);
                    lvEmployee.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lvEmployee);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.add(stringRequest);
    }
    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
    public void ChangePassword(){
        String userid = userDetails.UserId;
        String txtOldPass = etOldPassword.getText().toString();
        String txtNewPass = etNewPassword.getText().toString();
        String txtConfPass = etConfirmPassword.getText().toString();
        String urlsubmit = URL + "user-changepass";

        if (txtOldPass.equals("")){
            Toast.makeText(MainActivity.this, "Please enter Old Password", Toast.LENGTH_SHORT).show();
        } else {
            if (!txtNewPass.equals(txtConfPass)) {
                Toast.makeText(MainActivity.this, "New Password and Confirm Password do not Match", Toast.LENGTH_LONG).show();
            }else {
                HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stErrorMassage = response.toString();
                        showAlertDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("user_id", userid);
                        data.put("oldpassword", txtOldPass);
                        data.put("newpassword", txtNewPass);
                        data.put("newpassword_confirmation", txtNewPass);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        }
    }
    private void schedulePeriodicWork() throws ExecutionException, InterruptedException {
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
        WorkManager.getInstance(MainActivity.this).getWorkInfosForUniqueWork("notification_work")
                .get().forEach(info -> Log.d("WorkerState", info.getState().toString()));
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stErrorMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ApproveLeave extends AppCompatActivity {

    TextView tvTotalLeaves, tvLeaves, tvBalanceleaves, tvEmployeeName;
    ListView lvDates, lvEmployeenamesanddate;
    LinearLayout llListAppliedemployee;
    EditText etApproveRemark;
    Button btnApprove, btnDecline, btnCancel;
    String employeeName, leaveidleaveappId, leaveId, leaveType, stTotalLeaves, stLeaves, stBallance,
            stUserId, stApproveDecline, stApproveRemark, stMassage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_leave);

        Bundle bundle = getIntent().getExtras();

        employeeName = bundle.getString("employeename");
        leaveidleaveappId = bundle.getString("leaveidleaveappid");
        leaveType = bundle.getString("leavetype");
        leaveId = bundle.getString("leaveid");
        stUserId = bundle.getString("userid");

        tvEmployeeName = findViewById(R.id.tvEmployeeName);
        tvTotalLeaves = findViewById(R.id.tvApproveTotalLeaves);
        tvLeaves = findViewById(R.id.tvApproveLeaves);
        tvBalanceleaves = findViewById(R.id.tvApproveBalanceleaves);
    //    lvDates = findViewById(R.id.lvDates);
        etApproveRemark = findViewById(R.id.etApproveLeaveRemark);
        btnApprove = findViewById(R.id.btnApproveleave);
        btnDecline = findViewById(R.id.btnDeclineleave);
        btnCancel = findViewById(R.id.btnApproveCancel);

        getLeave();
        getLeaveDates();
        getLeaveEmps();
        tvEmployeeName.setText(employeeName);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iemp = new Intent(ApproveLeave.this, MainActivity.class);
                startActivity(iemp);
                finish();
            }
        });
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnApprove.setEnabled(false);
                stApproveDecline = "1";
                approvedeclineLeave();
                stMassage = "Leave Approved";
            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stApproveDecline = "2";
                approvedeclineLeave();
                stMassage = "Leave Decline";
            }
        });
    }

    private void getLeave(){
        String url = PublicURL + "getleaves.php?userid="+stUserId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] data = response.split("#");
                stTotalLeaves = data[0];
                stLeaves = data[1];
                stBallance = data[2];

                tvTotalLeaves.setText(stTotalLeaves);
                tvLeaves.setText(stLeaves);
                tvBalanceleaves.setText(stBallance);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.add(stringRequest);
    }
    public void setListViewHeightBasedOnChildren(ListView lvDates) {
        ListAdapter listAdapter = lvDates.getAdapter();
        if (listAdapter == null) {
            // Pre-condition
            return;
        }

        int totalHeight = 0;
        int itemCount = listAdapter.getCount();

        for (int i = 0; i < itemCount; i++) {
            View listItem = listAdapter.getView(i, null, lvDates);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lvDates.getLayoutParams();
        params.height = totalHeight + (lvDates.getDividerHeight() * (itemCount - 1));
        lvDates.setLayoutParams(params);
        lvDates.requestLayout();
    }
    public void setListViewHeightBasedOnChildren1(ListView lvEmployeenamesanddate) {
        ListAdapter listAdapter1 = lvEmployeenamesanddate.getAdapter();
        if (listAdapter1 == null) {
            // Pre-condition
            return;
        }

        int totalHeight = 0;
        int itemCount = listAdapter1.getCount();

        for (int i = 0; i < itemCount; i++) {
            View listItem = listAdapter1.getView(i, null, lvEmployeenamesanddate);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lvEmployeenamesanddate.getLayoutParams();
        params.height = totalHeight + (lvEmployeenamesanddate.getDividerHeight() * (itemCount - 1));
        lvEmployeenamesanddate.setLayoutParams(params);
        lvEmployeenamesanddate.requestLayout();
    }

    private void getLeaveDates(){
        String url = PublicURL + "getleavedates.php?leaveid="+leaveId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String[] dates = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dates[i] = jsonArray.getString(i);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ApproveLeave.this, android.R.layout.simple_list_item_1, dates);
                    lvDates = findViewById(R.id.lvDates);
                    lvDates.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lvDates);
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
    private void getLeaveEmps(){
        String url = PublicURL + "gatleaveappliedemplist.php?leaveid="+leaveId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                llListAppliedemployee.setVisibility(View.VISIBLE);
                lvEmployeenamesanddate.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String[] emps = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        emps[i] = jsonArray.getString(i);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ApproveLeave.this, android.R.layout.simple_list_item_1, emps);
                    ListView lvEmployeenamesanddate = findViewById(R.id.lvEmployeenamesanddate);
                    lvEmployeenamesanddate.setAdapter(adapter);
                    setListViewHeightBasedOnChildren1(lvEmployeenamesanddate);
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
    private void approvedeclineLeave(){

        stApproveRemark = etApproveRemark.getText().toString();

        String urlsubmit = PublicURL + "approve_decline.php";

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {

                    showAlertDialog();
                //    Toast.makeText(ApproveLeave.this, response.toString().trim(), Toast.LENGTH_SHORT).show();

                } else if (response.equals("failure")){
                    stMassage = "Error Applying Leave";
                    showAlertDialog();
                //    Toast.makeText(ApproveLeave.this, "Error Applying Leave", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ApproveLeave.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("approveduserid", UserId);
                data.put("remark", stApproveRemark);
                data.put("approvedecline", stApproveDecline);
                data.put("leaveid", leaveId);
                return data;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,3,1.0f));
        requestQueue.add(stringRequest);

    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ApproveLeave.this, ApproveLeavesList.class);
                startActivity(i);
                finish();
            }

        });
        builder.setCancelable(false);// Prevent dismissing the dialog by tapping outside

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
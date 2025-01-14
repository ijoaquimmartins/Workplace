package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveLeave extends AppCompatActivity {
    TextView tvTotalLeaves, tvLeaves, tvBalanceleaves, tvEmployeeName, tvEmpRemark, tvHeader;
    LinearLayout llListAppliedemployee, llLeaveDetails;
    EditText etApproveRemark;
    Button btnApprove, btnDecline, btnBack, btnCancel;
    String employeeName, leaveidleaveappId, leaveId, leaveType, stTotalLeaves, stLeaves, stBallance,
            stUserId, stApproveDecline, stApproveRemark, stMassage, stType;
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
        stType = bundle.getString("type");
        tvEmployeeName = findViewById(R.id.tvEmployeeName);
        tvTotalLeaves = findViewById(R.id.tvApproveTotalLeaves);
        tvLeaves = findViewById(R.id.tvApproveLeaves);
        tvBalanceleaves = findViewById(R.id.tvApproveBalanceleaves);
        tvEmpRemark = findViewById(R.id.tvEmpRemark);
        etApproveRemark = findViewById(R.id.etApproveLeaveRemark);
        llListAppliedemployee = findViewById(R.id.llListAppliedemployee);
        tvHeader = findViewById(R.id.tvHeader);
        llLeaveDetails =findViewById(R.id.llLeaveDetails);
        btnApprove = findViewById(R.id.btnApproveleave);
        btnDecline = findViewById(R.id.btnDeclineleave);
        btnBack = findViewById(R.id.btnApproveBack);
        btnCancel = findViewById(R.id.btnCancleleave);
        getLeave();
        getLeaveDates();
        tvEmployeeName.setText(employeeName);
        if(stType.equals("1")){
            getLeaveEmps();
            btnApprove.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
        } else if (stType.equals("2")) {
            llLeaveDetails.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
            etApproveRemark.setEnabled(false);
            tvHeader.setText("Leave");
            tvEmployeeName.setVisibility(View.GONE);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancleleave();
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
        String url = PublicURL + "fatchleavedetails.php?leaveid="+leaveId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    String status = jsonObject.getString("status");
                    String empremerk = jsonObject.getString("empremerk");
                    String aproremark = jsonObject.getString("aproremark");
                    tvEmpRemark.setText(empremerk);
                    etApproveRemark.setText(aproremark);
                    if(status.equals("0") && stType.equals("2")){
                        btnCancel.setVisibility(View.VISIBLE);
                    }
                    String[] dateStrings = jsonObject.getString("dates").split(",");
                    List<String> datesList = new ArrayList<>(Arrays.asList(dateStrings));
                    datesList.clear();
                    datesList.addAll(Arrays.asList(jsonObject.getString("dates").split(",")));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ApproveLeave.this, R.layout.list_item, R.id.tvItem, datesList);
                    ListView lvDates = findViewById(R.id.lvDates);
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
        request.add(jsonArrayRequest);
    }
    private void getLeaveEmps(){
        String url = PublicURL + "gatleaveappliedemplist.php?leaveid="+leaveId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String[] emps = new String[jsonArray.length()];
                    if (jsonArray.length() == 0) {
                        llListAppliedemployee.setVisibility(View.GONE);
                    }else{
                        llListAppliedemployee.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            emps[i] = jsonArray.getString(i);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ApproveLeave.this, android.R.layout.simple_list_item_1, emps);
                        ListView lvEmployeenamesanddate = findViewById(R.id.lvEmployeenamesanddate);
                        lvEmployeenamesanddate.setAdapter(adapter);
                        setListViewHeightBasedOnChildren1(lvEmployeenamesanddate);
                    }
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
                } else if (response.equals("failure")){
                    stMassage = "Error Applying Leave";
                    showAlertDialog();
                }else{
                    stMassage = "Please Contact Admin";
                    showAlertDialog();
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
    private void cancleleave(){
        String url = PublicURL + "cancleappliedleave.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String error = jsonResponse.optString("error", "");
                            String message = jsonResponse.optString("message", "");
                            if(error.equals("")){
                                stMassage=message.toString();
                                showAlertDialog();
                            }else{
                                stMassage=error.toString();
                                showAlertDialog();
                            }

                        } catch (Exception e) {
                            Log.e("JSON Parse Error", "Error parsing JSON response: " + e.getMessage());
                            Toast.makeText(ApproveLeave.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ApproveLeave.this, "Error deleting request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leaveid", leaveId);
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        requestQueue.add(stringRequest);
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ApproveLeave.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
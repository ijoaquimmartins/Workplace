package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.URL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class TotalLeave extends AppCompatActivity {

    DatePickerDialog picker;
    private ExpandableListView expandableListView;
    TextView tvFromDate, tvToDate;
    Button btnGet, btnSend;
    String stFromDate, stToDate, stButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_leave);

        expandableListView = findViewById(R.id.expandableListView);
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnGet = findViewById(R.id.btnGet);
        btnSend = findViewById(R.id.btnSend);
        stButton="GET";

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar cldr1 = java.util.Calendar.getInstance();
                int day1 = cldr1.get(java.util.Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(java.util.Calendar.MONTH);
                int year1 = cldr1.get(java.util.Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TotalLeave.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                tvFromDate.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                stFromDate = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar cldr1 = java.util.Calendar.getInstance();
                int day1 = cldr1.get(java.util.Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(java.util.Calendar.MONTH);
                int year1 = cldr1.get(java.util.Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TotalLeave.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                tvToDate.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                stToDate = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stButton="GET";
                if (!stFromDate.equals("") && !stToDate.equals("")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);

                        Date fromDate = sdf.parse(tvFromDate.getText().toString());
                        Date toDate = sdf.parse(tvToDate.getText().toString());
                        if (fromDate.before(toDate)) {
                            fetchEmployeeData();
                        } else {
                            Toast.makeText(TotalLeave.this, "Please check dates", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TotalLeave.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TotalLeave.this, "Please enter dates", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSend.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1); // Add 1 day for tomorrow

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String tomorrowDate = sdf.format(calendar.getTime());
            stFromDate=stToDate=tomorrowDate;
            stButton="SEND";
            fetchEmployeeData();
        });
        fetchEmployeeData();
    }

    private void fetchEmployeeData() {
        String url = URL + "fetch-leaves";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();

        JSONObject postParams = new JSONObject();
        try {
            postParams.put("fromdate", stFromDate);
            postParams.put("todate", stToDate);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON Response", response.toString());
                            JSONArray dataArray = response.optJSONArray("data");

                            if (dataArray == null || dataArray.length() == 0) {
                                Toast.makeText(TotalLeave.this, "No data available for selected dates.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            LinkedHashMap<String, List<String>> leaveDataMap = new LinkedHashMap<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject leaveObject = dataArray.getJSONObject(i);
                                String leaveDate = leaveObject.getString("leave_dates");
                                String employeeNames = leaveObject.getString("name");

                                List<String> employeeNameList = new ArrayList<>();
                                for (String name : employeeNames.split(",")) {
                                    employeeNameList.add(name.trim());
                                }

                                leaveDataMap.put(leaveDate, employeeNameList);

                                // SEND message
                                if ("SEND".equals(stButton)) {
                                    String names = TextUtils.join("\n", employeeNameList);
                                    String result = "*_Employees on leave " + leaveDate + "_*" + "\n" + names;
                                    sendMessageToGroup("MSS ATTENDANCE", result);
                                }
                            }
                            // Adapter to display the data in ExpandableListView
                            ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
                                private final List<String> groupList = new ArrayList<>(leaveDataMap.keySet());
                                private final HashMap<String, List<String>> childMap = new HashMap<>(leaveDataMap);

                                @Override
                                public int getGroupCount() {
                                    return groupList.size();
                                }

                                @Override
                                public int getChildrenCount(int groupPosition) {
                                    List<String> children = childMap.get(groupList.get(groupPosition));
                                    return children != null ? children.size() : 0;
                                }

                                @Override
                                public Object getGroup(int groupPosition) {
                                    return groupList.get(groupPosition);
                                }

                                @Override
                                public Object getChild(int groupPosition, int childPosition) {
                                    return childMap.get(groupList.get(groupPosition)).get(childPosition);
                                }

                                @Override
                                public long getGroupId(int groupPosition) {
                                    return groupPosition;
                                }

                                @Override
                                public long getChildId(int groupPosition, int childPosition) {
                                    return childPosition;
                                }

                                @Override
                                public boolean hasStableIds() {
                                    return true;
                                }

                                @Override
                                public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                                    if (convertView == null) {
                                        convertView = getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, null);
                                    }
                                    TextView groupTitle = convertView.findViewById(android.R.id.text1);
                                    groupTitle.setText(groupList.get(groupPosition));
                                    groupTitle.setTextColor(Color.parseColor("#3377ff"));
                                    groupTitle.setTextSize(24);
                                    return convertView;
                                }

                                @Override
                                public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                                    if (convertView == null) {
                                        convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
                                    }
                                    TextView childText = convertView.findViewById(android.R.id.text1);
                                    childText.setText(childMap.get(groupList.get(groupPosition)).get(childPosition));
                                    childText.setPadding(10, 10, 10, 10);
                                    childText.setTextSize(18);
                                    return convertView;
                                }

                                @Override
                                public boolean isChildSelectable(int groupPosition, int childPosition) {
                                    return true;
                                }
                            };

                            expandableListView.setAdapter(adapter);
                            for (int i = 0; i < adapter.getGroupCount(); i++) {
                                expandableListView.expandGroup(i);
                            }
                            setExpandableListViewHeightBasedOnChildren(expandableListView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Parsing Error", "Error parsing data: " + e.getMessage());
                            Toast.makeText(TotalLeave.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(TotalLeave.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    public void setExpandableListViewHeightBasedOnChildren(ExpandableListView expandableListView) {
        ExpandableListAdapter listAdapter = expandableListView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int itemCount = listAdapter.getGroupCount();
        for (int i = 0; i < itemCount; i++) {
            View groupItem = listAdapter.getGroupView(i, true, null, expandableListView);
            groupItem.measure(0, 0);
            totalHeight += groupItem.getMeasuredHeight();
            if (expandableListView.isGroupExpanded(i)) {
                int childCount = listAdapter.getChildrenCount(i);
                for (int j = 0; j < childCount; j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, expandableListView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = totalHeight + (expandableListView.getDividerHeight() * (itemCount - 1));
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }
    public void sendMessageToGroup(String groupName, String message) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setPackage("com.whatsapp");

            // Limitations: Only opens WhatsApp, doesn't auto-send
            startActivity(Intent.createChooser(sendIntent, "Share with"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

}

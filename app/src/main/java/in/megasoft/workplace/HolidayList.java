package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HolidayList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spnSelectYear;
    ExpandableListView lvHolidayList;
    String stYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_holiday_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spnSelectYear = findViewById(R.id.spnSelectYear);
        lvHolidayList = findViewById(R.id.lvHolidayList);

        List<String> years = getYearsList(2012);
        years.add(0, "Upcoming");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectYear.setAdapter(adapter);

    }
    private List<String> getYearsList(int startYear) {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = startYear; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        return years;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        stYear = spnSelectYear.toString();
        HolidayList();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void HolidayList(){
        String url = PublicURL + "fatchholiday.php?styear=" + stYear;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON Response", response.toString()); // Log response for troubleshooting
                            JSONArray dataArray = response.optJSONArray("data");
                            if (dataArray == null || dataArray.length() == 0) {
                                Toast.makeText(HolidayList.this, "No data available for selected dates.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Prepare data for the expandable list
                            LinkedHashMap<String, List<String>> leaveDataMap = new LinkedHashMap<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject leaveObject = dataArray.getJSONObject(i);
                                // Extract leave_date and name
                                String leaveDate = leaveObject.getString("leave_dates");
                                String employeeNames = leaveObject.getString("name");
                                // Split names by comma and add to list
                                List<String> employeeNameList = Arrays.asList(employeeNames.split(","));
                                leaveDataMap.put(leaveDate, employeeNameList);
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
                                    return childMap.get(groupList.get(groupPosition)).size();
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
                            lvHolidayList.setAdapter(adapter);
                            for (int i = 0; i < adapter.getGroupCount(); i++) {
                                lvHolidayList.expandGroup(i);
                            }
                            setExpandableListViewHeightBasedOnChildren(lvHolidayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON Parsing Error", "Error parsing data: " + e.getMessage());
                            Toast.makeText(HolidayList.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(HolidayList.this, "Error fetching data", Toast.LENGTH_SHORT).show();
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


}
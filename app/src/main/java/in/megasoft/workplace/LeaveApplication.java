package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;
import static in.megasoft.workplace.userDetails.UserName;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveApplication extends AppCompatActivity implements OnSelectDateListener {
    public static ArrayList<String> datearrayList = new ArrayList<String>();
    Spinner spinLeaveType, spinLeaveApplicationType;
    TextView datesTV, tvTotalLeaves, tvLeaves, tvBalanceleaves, tvTypeCount;
    EditText etRemark;
    Button btnDatePicker, btnApplyLeave, btnCancel;
    String stTotalLeaves, stLeaves, stBallance, stLeaveType, stDates, stremark,
            stLeave, selectedId, stTotalCount1, stMassage, stResponse, stDateEmp;
    String[] leavetype1 = {"Select", "Full Day", "Morning Half", "Evening Half"};
    String[] leavetype2 = {"Select", "Full Day"};
    String[] leavetype = {"Select"};
    Calendar min, max;
    ArrayList datearray = new ArrayList<>();
    ArrayList newDateArray = new ArrayList<>();
    LinearLayout layZeroLeave;
    ArrayAdapter leavead;
    JSONArray leavedates = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);

        etRemark = findViewById(R.id.etLeaveRemark);
        datesTV = findViewById(R.id.tvSelectedDate);
        tvTotalLeaves = findViewById(R.id.tvTotalLeaves);
        tvLeaves = findViewById(R.id.tvLeaves);
        tvBalanceleaves = findViewById(R.id.tvBalanceleaves);
        tvTypeCount = findViewById(R.id.tvTypeCount);
        spinLeaveApplicationType = findViewById(R.id.spinLeaveApplicationType);
        spinLeaveType = findViewById(R.id.spinLeaveType);
        btnApplyLeave = findViewById(R.id.btnSubmitleave);
        btnCancel = findViewById(R.id.btnCancelleave);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        layZeroLeave.findViewById(R.id.layZeroLeave);
        btnApplyLeave.setVisibility(View.GONE);
        getLeave();
        getLeaveType();

        spinLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LeaveType selectedLeaveType = (LeaveType) adapterView.getItemAtPosition(i);
                selectedId = selectedLeaveType.getId();
                String stLeaveType = selectedLeaveType.getLeaveType();
                if(stLeaveType.equals("WFH")){
                    leavetype=leavetype2;
                     leavead  = new ArrayAdapter(LeaveApplication.this, android.R.layout.simple_spinner_item, leavetype);
                    leavead.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinLeaveApplicationType.setAdapter(leavead);
                }else {
                    leavetype=leavetype1;
                     leavead  = new ArrayAdapter(LeaveApplication.this, android.R.layout.simple_spinner_item, leavetype);
                    leavead.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinLeaveApplicationType.setAdapter(leavead);
                }
                TypeCount();
                tvTypeCount.setText(stTotalCount1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinLeaveApplicationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                handleSpinnerSelection(selectedItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btnDatePicker.setEnabled(false);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LeaveApplication.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stLeaveType.equals("fullday")){
                    min = Calendar.getInstance();
                    min.add(Calendar.DAY_OF_MONTH, -1);
                    max = Calendar.getInstance();
                    max.add(Calendar.DAY_OF_MONTH, -1);
                    openManyDaysPicker();
                } else if (stLeaveType.equals("mhalfday")) {
                    min = Calendar.getInstance();
                    min.add(Calendar.DAY_OF_MONTH, -1);
                    max = Calendar.getInstance();
                    max.add(Calendar.DAY_OF_MONTH, -1);
                    openManyDaysPicker();
                } else if(stLeaveType.equals("ehalfday")) {
                    min = Calendar.getInstance();
                    min.add(Calendar.DAY_OF_MONTH, -1);
                    max = Calendar.getInstance();
                    max.add(Calendar.DAY_OF_MONTH, -1);
                    openManyDaysPicker();
                }
            }
        });
        btnApplyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyleave();
            }
        });
    }
    public class LeaveType {
        private String id;
        private String leaveType;
        public LeaveType(String id, String leaveType) {
            this.id = id;
            this.leaveType = leaveType;
        }
        public String getId() {
            return id;
        }
        public String getLeaveType() {
            return leaveType;
        }
        @Override
        public String toString() {
            return leaveType; // This will be displayed in the spinner
        }
    }
    public void getLeaveType() {
        final List<LeaveType> leaveTypeList = new ArrayList<>();
        String url = PublicURL + "getleavetype.php?userid=" + userDetails.UserId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String leaveType = jsonObject.getString("leave_type");
                        leaveTypeList.add(new LeaveType(id, leaveType));
                    }
                    ArrayAdapter<LeaveType> leaveTypeAdapter = new ArrayAdapter<>(LeaveApplication.this, android.R.layout.simple_spinner_item, leaveTypeList);
                    leaveTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinLeaveType.setAdapter(leaveTypeAdapter);
                    spinLeaveType.setSelection(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LeaveApplication.this, "Error fetching leave types", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(stringRequest);

    }
    private void handleSpinnerSelection(String item) {
        switch (item) {
            case "Select":
                btnDatePicker.setEnabled(false);
                datesTV.setText("");
                datearray.clear();
                newDateArray.clear();
                break;
            case "Full Day":
                stLeaveType = "fullday";
                stLeave = "0";
                btnDatePicker.setEnabled(true);
                datesTV.setText("");
                datearray.clear();
                newDateArray.clear();
                break;
            case "Morning Half":
                stLeaveType = "mhalfday";
                stLeave = "1";
                btnDatePicker.setEnabled(true);
                datesTV.setText("");
                datearray.clear();
                newDateArray.clear();
                break;
            case "Evening Half":
                stLeaveType = "ehalfday";
                stLeave = "2";
                btnDatePicker.setEnabled(true);
                datesTV.setText("");
                datearray.clear();
                newDateArray.clear();
                break;
            default:
                Toast.makeText(this, "Please Select Leave Type", Toast.LENGTH_LONG).show();
                break;
        }
    }
    private void openManyDaysPicker() {
        List<Calendar> selectedDays = new ArrayList<>(getDisabledDays());
        selectedDays.add(min);
        selectedDays.add(max);
        DatePickerBuilder manyDaysBuilder = new DatePickerBuilder((Context) this, this)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .headerColor(android.R.color.holo_green_dark)
                .selectionColor(android.R.color.holo_green_dark)
                .todayLabelColor(android.R.color.holo_green_dark)
                .dialogButtonsColor(android.R.color.holo_green_dark)
                .selectedDays(selectedDays)
                .firstDayOfWeek(CalendarWeekDay.SUNDAY)
                .navigationVisibility(View.VISIBLE)
                .disabledDays(getDisabledDays());

        DatePicker manyDaysPicker = manyDaysBuilder.build();
        manyDaysPicker.show();
    }

    private List<Calendar> getDisabledDays() {

        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, -2);
        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, -3);
        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, -1);
        List<Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);

        Calendar today = DateUtils.getCalendar();
        for (int i = 1; i <= 500; i++) {
            Calendar day = (Calendar) today.clone();
            day.add(Calendar.DAY_OF_YEAR, -i);
                calendars.add(day);
        }
        for (int j = 0; j <= 365; j++) {
            Calendar day = (Calendar) today.clone();
            day.add(Calendar.DAY_OF_YEAR, j);
            if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendars.add(day);
            }
        }
        return calendars;
    }
    private void addDisabledDay(List<Calendar> calendars, int offset) {
        Calendar disabledDay = (Calendar) DateUtils.getCalendar().clone();
        disabledDay.add(Calendar.DAY_OF_MONTH, offset);
        calendars.add(disabledDay);
    }
    @Override
    public void onSelect(@NotNull List<Calendar> calendars) {

        datearray.clear();
        newDateArray.clear();
        leavedates = new JSONArray();
        if (calendars.size() > 0) {
            if (stLeaveType.equals("fullday")) {
                if (calendars.size() < 6) {
                        btnApplyLeave.setVisibility(View.VISIBLE);
                        Stream.of(calendars).forEach(calendar -> {
                        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                        int month1 = calendar.get(Calendar.MONTH);
                        int year1 = calendar.get(Calendar.YEAR);

                        datearray.add(day1 + "/" + (month1 + 1) + "/" + year1);
                        newDateArray.add(year1 + "-" + (month1 + 1) + "-" + day1);
                        leavedates.put(year1 + "-" + (month1 + 1) + "-" + day1);
                        stDateEmp = leavedates.toString();
                        getLeaveEmps();
                    });
                } else {
                    stMassage = "Maximum 5 Days Only through the app";
                    showAlertDialog();
                }
            } else if (stLeaveType.equals("mhalfday") || stLeaveType.equals("ehalfday")) {
                if(calendars.size() == 1){
                    btnApplyLeave.setVisibility(View.VISIBLE);
                    Stream.of(calendars).forEach(calendar -> {
                        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                        int month1 = calendar.get(Calendar.MONTH);
                        int year1 = calendar.get(Calendar.YEAR);
                        datearray.add(day1 + "/" + (month1 + 1) + "/" + year1);
                        newDateArray.add(year1 + "-" + (month1 + 1) + "-" + day1);
                        leavedates.put(year1 + "-" + (month1 + 1) + "-" + day1);
                        stDateEmp = leavedates.toString();
                        getLeaveEmps();
                    });
                }else{
                    stMassage = "You can apply only 1 half day at a time";
                    showAlertDialog();
                }
            }
        }else{
            stMassage = "No days selected";
            showAlertDialog();
        }
        datesTV.setText(datearray.toString());
        stDates = leavedates.toString();
    }
    private void getLeave(){
        String url = PublicURL + "getleaves.php?userid="+userDetails.UserId;
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
                if(stBallance.equals("0")){
                    layZeroLeave.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.add(stringRequest);
    }
    public void setListViewHeightBasedOnChildren(ListView lvEmployeenames) {
        ListAdapter listAdapter1 = lvEmployeenames.getAdapter();
        if (listAdapter1 == null) {
            return;
        }
        int totalHeight = 0;
        int itemCount = listAdapter1.getCount();
        for (int i = 0; i < itemCount; i++) {
            View listItem = listAdapter1.getView(i, null, lvEmployeenames);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lvEmployeenames.getLayoutParams();
        params.height = totalHeight + (lvEmployeenames.getDividerHeight() * (itemCount - 1));
        lvEmployeenames.setLayoutParams(params);
        lvEmployeenames.requestLayout();
    }
    private void getLeaveEmps(){
        String url = PublicURL + "getappliedemplist.php?leavedates="+stDateEmp;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response",response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String[] emps = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        emps[i] = jsonArray.getString(i);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LeaveApplication.this, android.R.layout.simple_list_item_1, emps);
                    ListView lvEmployeenames = findViewById(R.id.lvEmployeenames);
                    lvEmployeenames.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lvEmployeenames);
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
    public void applyleave(){
        stremark = etRemark.getText().toString();
        String urlsubmit = PublicURL + "apply_leave.php";
        if(!spinLeaveType.equals("")){
            if(!datesTV.equals("") && !etRemark.equals("")) {
                in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            stResponse = response.toString();
                            stMassage = "Leave Applied "+leavedates.toString();
                            showAlertDialog();
                        } else if (response.equals("failure")){
                            stMassage = "Error Applying Leave";
                            showAlertDialog();
                        }else {
                            showCustomDialog(response.toString().trim());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveApplication.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("datestv", leavedates.toString());
                        data.put("etremark", stremark);
                        data.put("userid", userDetails.UserId.toString());
                        data.put("leavetype", stLeave);
                        data.put("stleavetype", selectedId);
                        data.put("username", UserName);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,3,1.0f));
                requestQueue.add(stringRequest);
            }else{
                stMassage = "Please select date";
                showAlertDialog();
            }
        } else {
            stMassage = "Please select leave type";
            showAlertDialog();
        }
    }
    private void showCustomDialog(String errordates) {
        TextView title, massage;

        // Create a dialog
        final Dialog dialog = new Dialog(LeaveApplication.this);
        dialog.setContentView(R.layout.custom_d);

        title = dialog.findViewById(R.id.dialog_title);
        massage = dialog.findViewById(R.id.massage_message);
        Button submitButton = dialog.findViewById(R.id.btnOk);

        massage.setText(errordates);

        title.setText("Error");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void TypeCount(){
        String getcount;
        getcount = selectedId+"|"+UserId;

        String url = PublicURL + "getleavestypecount.php?getcount="+getcount;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stTotalCount1 = response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("success".equals(stResponse)){
                    startActivity(new Intent(LeaveApplication.this, MainActivity.class));
                }else{
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
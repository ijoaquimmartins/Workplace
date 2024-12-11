package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.*;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AttendanceDetails extends AppCompatActivity {
    TableLayout tableLayout;
    Spinner spnMonth, spnYear;
    String stMonth, stYear, stErrorMassage;
    Button btnGetAttendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        String[] listMonth = {"Select Month","JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
        tableLayout = findViewById(R.id.tlAttendance);
        spnMonth = findViewById(R.id.spnMonth);
        spnYear = findViewById(R.id.spnYear);
        btnGetAttendance = findViewById(R.id.btnGetAttendance);

        ArrayAdapter<String> monthadapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                listMonth
        );
        monthadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonth.setAdapter(monthadapter);

        List<String> years = getYearsList(2024);
        years.add(0, "Select Year");
        ArrayAdapter<String> yearadapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYear.setAdapter(yearadapter);

        btnGetAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stMonth = spnMonth.getSelectedItem().toString();
                stYear = spnYear.getSelectedItem().toString();
                if(stMonth.equals("Select Month")){
                    stErrorMassage="Please Select Month";
                    showAlertDialog();
                } else if (stYear.equals("Select Year")) {
                    stErrorMassage="Please select year";
                    showAlertDialog();
                }else {
                    getAttendance();
                }
            }
        });
    }
    private List<String> getYearsList(int startYear) {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = startYear; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        return years;
    }
    public void getAttendance(){
        tableLayout.removeAllViews();
        String url = URL + "get-attendance-data";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonData = response.toString();
                        try {
                            JSONArray jsonArray = new JSONArray(jsonData);
                            JSONObject firstObject = jsonArray.getJSONObject(0);
                            Iterator<String> keys = firstObject.keys();
                            TableRow headerRow = new TableRow(AttendanceDetails.this);
                            TextView nameHeader = new TextView(AttendanceDetails.this);
                            nameHeader.setText("Name");
                            nameHeader.setPadding(16, 16, 16, 16);
                            headerRow.addView(nameHeader);
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (!key.equals("name")) {
                                    TextView headerCell = new TextView(AttendanceDetails.this);
                                    headerCell.setText(key.toUpperCase());
                                    headerCell.setPadding(16, 16, 16, 16);
                                    headerRow.addView(headerCell);
                                }
                            }
                            tableLayout.addView(headerRow);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TableRow dataRow = new TableRow(AttendanceDetails.this);
                                JSONObject rowObject = jsonArray.getJSONObject(i);
                                TextView nameCell = new TextView(AttendanceDetails.this);
                                nameCell.setText(rowObject.getString("name"));
                                nameCell.setPadding(16, 16, 16, 16);
                                dataRow.addView(nameCell);
                                keys = rowObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (!key.equals("name")) {
                                        JSONObject dayObject = rowObject.getJSONObject(key);
                                        TextView cell = new TextView(AttendanceDetails.this);
                                        cell.setText(dayObject.getString("status"));
                                        cell.setPadding(16, 16, 16, 16);
                                        if (dayObject.getBoolean("isSunday")) {
                                            cell.setBackgroundColor(Color.GRAY);
                                        } else if (dayObject.getBoolean("isHoliday")) {
                                            cell.setBackgroundColor(Color.RED);
                                        } else {
                                            cell.setBackgroundColor(Color.TRANSPARENT);
                                        }
                                        dataRow.addView(cell);
                                    }
                                }
                                tableLayout.addView(dataRow);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AttendanceDetails.this, "Error fetching request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("month", stMonth);
                params.put("year", stYear);
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



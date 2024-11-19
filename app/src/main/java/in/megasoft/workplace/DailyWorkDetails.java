package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DailyWorkDetails extends AppCompatActivity {
    Context context;
    String url;
    private static final int REQUEST_CODE = 1;
    private GridView gridView;
    DatePickerDialog picker;
    TextView tvFromDate, tvToDate;
    String stFromDate, stToDate, stMassage;
    Button btnGet, btnExcelExport;
    String downloadUrl = PublicURL + "dailyreport.php";
    private String jsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_work_details); // Always set the content view first!

        // Now initialize views
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        btnGet = findViewById(R.id.btnGetData);
        gridView = findViewById(R.id.gridView);
        btnExcelExport = findViewById(R.id.btnExcelExport);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String formattedDate = dateFormat.format(cal.getTime());
        String newFormatedDate = newDateFormat.format(cal.getTime());

        tvFromDate.setText(newFormatedDate);
        tvToDate.setText(newFormatedDate);

        stFromDate = stToDate= formattedDate.toString();

        DailyWorkData();

        handleFilePermissions();

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                btnGet.setVisibility(View.GONE);
                tvToDate.setVisibility(View.GONE);
                // date picker dialog
                picker = new DatePickerDialog(DailyWorkDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                tvFromDate.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                stFromDate = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                                tvToDate.setVisibility(View.VISIBLE);
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });
        tvToDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                btnGet.setVisibility(View.GONE);
                btnExcelExport.setVisibility(View.GONE);
                // date picker dialog
                picker = new DatePickerDialog(DailyWorkDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                tvToDate.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                stToDate = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    sdf.setLenient(false);
                                    Date fromDate = sdf.parse(tvFromDate.getText().toString());
                                    Date toDate = sdf.parse(tvToDate.getText().toString());
                                    if (!fromDate.after(toDate)) {
                                        btnGet.setVisibility(View.VISIBLE);
                                    } else {
                                        stMassage="Please check dates";
                                        showAlertDialog();
                                    }
                                } catch (Exception e) {
                                    stMassage="Invalid Dates";
                                    showAlertDialog();
                                }
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DailyWorkData();
            }
        });
        btnExcelExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonData != null) {

                    openUrlInBrowser(context, downloadUrl);
                }else {
                    Toast.makeText (DailyWorkDetails.this, "Error:- No Data to load",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (jsonData == null) {
            jsonData = ""; // Initialize to avoid null
        }

        List<Map<String, String>> data = parseJson(jsonData);

        int columnCount = calculateColumnCount(data.size());
        gridView.setNumColumns(columnCount);
        GridAdapter adapter = new GridAdapter(this, data);
        gridView.setAdapter(adapter);
    }
    private int calculateColumnCount(int size) {
        if (size <= 5) {
            return 1;
        } else if (size <= 10) {
            return 2;
        } else {
            return 3;
        }
    }
    private List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> list = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            Toast.makeText(this, "No data available to parse.", Toast.LENGTH_SHORT).show();
            return list; // Return an empty list
        }

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                for (Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ) {
                    String key = keys.next();
                    map.put(key, jsonObject.getString(key));
                }
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    public void DailyWorkData() {
        String url = PublicURL + "dailyworkdetails.php";
        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonData = response;
                        btnExcelExport.setVisibility(View.VISIBLE);
                        if (jsonData != null && !jsonData.isEmpty()) {
                            List<Map<String, String>> data = parseJson(jsonData);
                            int columnCount = calculateColumnCount(data.size());
                            gridView.setNumColumns(columnCount);
                            GridAdapter adapter = new GridAdapter(DailyWorkDetails.this, data);
                            gridView.setAdapter(adapter);
                            btnGet.setVisibility(View.GONE);
                            btnExcelExport.setVisibility(View.VISIBLE);
                            // generateExcelFile(); // Generate Excel only after valid data is fetched
                        } else {
                            Toast.makeText(DailyWorkDetails.this, "No data received.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DailyWorkDetails.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("from_dt", stFromDate);
                data.put("to_dt", stToDate);
                return data;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 1.0f));
        requestQueue.add(stringRequest);
    }

    private void handleFilePermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                // Generate the file only after data is fetched
            }
        }
    }

    public void openUrlInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        this.context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //generateExcelFile();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save the file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
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
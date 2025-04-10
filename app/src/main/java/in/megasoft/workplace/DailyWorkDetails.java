package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.URL;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyWorkDetails extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private GridView gridView;
    DatePickerDialog picker;
    TextView tvFromDate, tvToDate;
    String stFromDate, stToDate, stMassage;
    Button btnGet, btnExcelExport;
    private String jsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_work_details);

        Permission();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

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

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                btnGet.setVisibility(View.GONE);
                tvToDate.setVisibility(View.GONE);
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
                picker = new DatePickerDialog(DailyWorkDetails.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                            tvToDate.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                            stToDate = year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1;

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                sdf.setLenient(false);
                                String fromDateStr = tvFromDate.getText().toString();
                                String toDateStr = tvToDate.getText().toString();

//                                if (!fromDateStr.matches("\\d{2}/\\d{2}/\\d{4}") || !toDateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
//                                    stMassage = "Invalid Date Format";
//                                    showAlertDialog();
//                                    return;
//                                }
                                Date fromDate = sdf.parse(fromDateStr);
                                Date toDate = sdf.parse(toDateStr);
                                if (!fromDate.after(toDate)) {
                                    btnGet.setVisibility(View.VISIBLE);
                                } else {
                                    stMassage = "Please check dates";
                                    showAlertDialog();
                                }
                            } catch (ParseException e) {
                                stMassage = "Invalid Dates";
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
                downloadFile();
            }
        });

        if (jsonData == null) {
            jsonData = "";
        }
        List<Map<String, String>> data = parseJson(jsonData);
        int columnCount = calculateColumnCount(data.size());
        gridView.setNumColumns(columnCount);
        GridAdapter adapter = new GridAdapter(this, data);
        gridView.setAdapter(adapter);
    }
    private int calculateColumnCount(int size) {
        if (size <= 5) {
            return 2;
        } else if (size <= 10) {
            return 2;
        } else {
            return 1;
        }
    }
    private List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> list = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            Toast.makeText(this, "No data available to parse.", Toast.LENGTH_SHORT).show();
            return list;
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
    public void Permission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(DailyWorkDetails.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DailyWorkDetails.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }
    public void downloadFile() {
        String url = URL + "employee-gen-report";
        HttpsTrustManager.allowAllSSL();

        Request<byte[]> request = new Request<byte[]>(Request.Method.POST, url,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DailyWorkDetails.this, "Download failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add POST parameters here
                Map<String, String> params = new HashMap<>();
                params.put("report_nm", "daily work");
                params.put("from_dt", stFromDate);
                params.put("to_dt", stToDate);
                return params;
            }
            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
            }
            @Override
            protected void deliverResponse(byte[] response) {
                try {
                    saveFileToDisk(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DailyWorkDetails.this, "Failed to save file.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
    private void saveFileToDisk(byte[] fileData) throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = "dwr_" + timestamp + ".xlsx";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(fileData);
        outputStream.close();
        stMassage = "File saved: " + file.getAbsolutePath();
        showAlertDialog();
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
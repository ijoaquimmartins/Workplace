package in.megasoft.workplace;

import static android.view.View.GONE;
import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.URL;
import static in.megasoft.workplace.userDetails.UserId;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MarkAttendance extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String attngetmarked;
    private String attnmarkedas;

    private String[] atten = {"Present", "E L"};
    private TextView datetime, errormassage, tvAttenDataTime, tvAttenAs, tvSelectDate, tvAdvanceElMark;
    private String attnspinvalue, newattnspinvalue, latitude, longitude, attnstatus, attendatetime, stMassage, stAttnStatus, tableId;
    private ImageView imgsetalight;
    private Button btnSubmitAttn, btncanceltAttn, btnMarkAdvEl, btnPresent, btnEl, btnBackAttn;
    private Spinner spinattnmark;
    private FusedLocationProviderClient mFusedLocationClient;

    private LinearLayout llApplyEL;
    private static final int PERMISSION_ID = 44;
    private static final long LOCATION_UPDATE_INTERVAL = 1000;
    private static final long LOCATION_FASTEST_INTERVAL = 500;
    private static final int MIN_DISTANCE = 30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);
        errormassage = findViewById(R.id.txtErrorMassage);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvAttenDataTime = findViewById(R.id.tvAttenDataTime);
        tvAttenAs = findViewById(R.id.tvAttenAs);

        tvAdvanceElMark = findViewById(R.id.tvAdvanceElMark);
        llApplyEL = findViewById(R.id.llApplyEL);
        tvSelectDate = findViewById(R.id.tvSelectDate);
        btnMarkAdvEl = findViewById(R.id.btnMarkAdvEl);
        btnPresent = findViewById(R.id.btnPresent);
        btnEl = findViewById(R.id.btnEl);
        btnBackAttn = findViewById(R.id.btnBackAttn);
        btncanceltAttn = findViewById(R.id.btncanceltAttn);

        getLastLocation();

        imgsetalight = findViewById(R.id.iconSatellite);
        datetime = findViewById(R.id.txtViewDatetime);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        datetime.setText(sdf.format(new Date()));

        spinattnmark = findViewById(R.id.spinAttnMark);
        spinattnmark.setOnItemSelectedListener(this);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, atten);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinattnmark.setAdapter(ad);

        btnSubmitAttn = findViewById(R.id.btnSubmitAttn);
        btnSubmitAttn.setOnClickListener(view -> submitbtn());

        btnBackAttn.setOnClickListener(view -> {
//            startActivity(new Intent(MarkAttendance.this, MainActivity.class));
            finish();
        });

        //tvAdvanceElMark.setOnClickListener(view -> llApplyEL.setVisibility(View.VISIBLE));
        tvAdvanceElMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llApplyEL.setVisibility(View.VISIBLE);
                openDatePicker();
            }
        });
        //tvSelectDate.setOnClickListener(view -> openDatePicker());
        btnMarkAdvEl.setOnClickListener(view -> MarkAdvanceEl());

        btnPresent.setOnClickListener(view -> {
            stAttnStatus="1";
            submitbtn();
        });

        btnEl.setOnClickListener(view -> {
            stAttnStatus="2";
            submitbtn();
        });
        btncanceltAttn.setOnClickListener(view -> cancelMarked());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        attnspinvalue = adapterView.getItemAtPosition(i).toString();
        switch (attnspinvalue) {
            case "Present":
                newattnspinvalue = "1";
                attngetmarked = "Present";
                break;
            case "E L":
                newattnspinvalue = "2";
                attngetmarked = "E L";
                break;
            default:
                newattnspinvalue = "6";
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData();
            } else {
                promptUserToEnableLocation();
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE); // Only update when location changes by 3 meters

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
        Location mLastLocation = locationResult.getLastLocation();
        handleLocation(mLastLocation);  // Handle new location
        }
    };

    private void handleLocation(Location location) {
        if (location != null) {
            imgsetalight.setImageResource(R.drawable.ic_satellite_live);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            getAttendance();
        } else {
            Toast.makeText(MarkAttendance.this, "Location is null, retrying...", Toast.LENGTH_SHORT).show();
        }
    }

    private void promptUserToEnableLocation() {
        Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    public void getAttendance() {
        String url = PublicURL + "atten_check.php?userid=" + UserId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            String[] data = response.split("\\|");
            attnstatus = data[0];
            attendatetime = data[1];
            tableId = data[2];
            updateAttendanceStatus(attnstatus, attendatetime);
        }, error -> Log.e("Volley Error", error.toString()));
        request.add(stringRequest);
    }

    private void updateAttendanceStatus(String attnstatus, String attendatetime) {
        switch (attnstatus) {
            case "0": case "1": case "2": case "3": case "4": case "5": case "6":
                tvAttenAs.setVisibility(View.VISIBLE);
                tvAttenDataTime.setVisibility(View.VISIBLE);
                tvAttenAs.setText(attendatetime);
                tvAttenDataTime.setText(attnmarkedas);
                spinattnmark.setVisibility(GONE);
                btnSubmitAttn.setVisibility(GONE);
                btnPresent.setVisibility(GONE);
                btnEl.setVisibility(GONE);
                break;
            case "7":
                tvAttenAs.setVisibility(GONE);
                tvAttenDataTime.setVisibility(GONE);
//                btnSubmitAttn.setVisibility(View.VISIBLE);
                btnPresent.setEnabled(true);
                btnEl.setEnabled(true);
                break;
        }
    }

    private void submitbtn() {

        getLastLocation();
        String attngetdatetime = datetime.getText().toString();
        String urlsubmit = URL + "attendance-save";

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("success")) {
                        Toast.makeText(MarkAttendance.this, jsonResponse.getString("success"), Toast.LENGTH_LONG).show();
                        userDetails.AttnMarkedAs = attngetmarked;
                        userDetails.AttnDateTime = attngetdatetime;
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MarkAttendance.this, "Response parsing error", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MarkAttendance.this, error.toString().trim(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("atten_status", stAttnStatus);
                data.put("userid", Base64.getEncoder().encodeToString((UserId.toString().trim()).getBytes()));
                data.put("location_let", latitude);
                data.put("location_long", longitude);
                return data;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
        private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // Calculate the next Saturday
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MarkAttendance.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date to the TextView
                    tvSelectDate.setText(day + "/" + (month + 1) + "/" + year);
                },
                year, month, day
        );

        // Restrict the selectable date to the next Saturday only
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Show the dialog
        datePickerDialog.setTitle("Select Next Saturday");
        datePickerDialog.show();
    }

    public void MarkAdvanceEl(){

        String elAvdDate = tvSelectDate.getText().toString();
        String urlsub = userDetails.PublicURL + "markadvancel.php";

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsub,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String error = jsonResponse.optString("error", "");
                            String message = jsonResponse.optString("message", "");
                            if(error.equals("")){
                                stMassage=message.toString();
                                btnMarkAdvEl.setVisibility(GONE);
                                showAlertDialog();
                            }else{
                                llApplyEL.setVisibility(GONE);
                                btnMarkAdvEl.setVisibility(GONE);
                                stMassage=error.toString();
                                showAlertDialog();
                            }
                        } catch (Exception e) {
                            Log.e("JSON Parse Error", "Error parsing JSON response: " + e.getMessage());
                            Toast.makeText(MarkAttendance.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MarkAttendance.this, "Error Adding request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("userid", userDetails.UserId);
                data.put("eldate", elAvdDate);
                return data;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
    private void cancelMarked(){
        String elAvdDate = tvSelectDate.getText().toString();
        String urlsub = userDetails.PublicURL + "cancelmarked.php";

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsub,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String error = jsonResponse.optString("error", "");
                            String message = jsonResponse.optString("message", "");
                            if(error.equals("")){
                                stMassage = message.toString();
                                btnMarkAdvEl.setVisibility(GONE);
                                showAlertDialog();
                            }else{
                                llApplyEL.setVisibility(GONE);
                                btnMarkAdvEl.setVisibility(GONE);
                                stMassage=error.toString();
                                showAlertDialog();
                            }
                        } catch (Exception e) {
                            Log.e("JSON Parse Error", "Error parsing JSON response: " + e.getMessage());
                            Toast.makeText(MarkAttendance.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MarkAttendance.this, "Error Adding request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("userid", userDetails.UserId);
                data.put("eldate", elAvdDate);
                return data;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}


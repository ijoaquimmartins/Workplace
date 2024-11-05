package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MarkAttendance extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String attngetmarked;
    private String attnmarkedas;

    private String[] atten = {"Present", "E L"};
    private TextView datetime, errormassage, tvAttenDataTime, tvAttenAs;
    private String attnspinvalue, newattnspinvalue, latitude, longitude, attnstatus, attendatetime;
    private ImageView imgsetalight;
    private Button btnSubmitAttn, btncanceltAttn;
    private Spinner spinattnmark;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int PERMISSION_ID = 44;
    private static final long LOCATION_UPDATE_INTERVAL = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        errormassage = findViewById(R.id.txtErrorMassage);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tvAttenDataTime = findViewById(R.id.tvAttenDataTime);
        tvAttenAs = findViewById(R.id.tvAttenAs);

        getAttendance();
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

        btncanceltAttn = findViewById(R.id.btncanceltAttn);
        btncanceltAttn.setOnClickListener(view -> {
            startActivity(new Intent(MarkAttendance.this, MainActivity.class));
            finish();
        });
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
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        handleLocation(location);
                    }
                });
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
        mLocationRequest.setFastestInterval(5000); // 5 seconds
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            handleLocation(mLastLocation);
        }
    };

    private void handleLocation(Location location) {
        imgsetalight.setImageResource(R.drawable.ic_satellite_live);
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        btnSubmitAttn.setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
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
                spinattnmark.setVisibility(View.GONE);
                btnSubmitAttn.setVisibility(View.GONE);
                break;
            case "7":
                tvAttenAs.setVisibility(View.GONE);
                tvAttenDataTime.setVisibility(View.GONE);
                spinattnmark.setVisibility(View.VISIBLE);
                btnSubmitAttn.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void submitbtn() {
        String attngetdatetime = datetime.getText().toString();
        String urlsubmit = userDetails.PublicURL + "attenmark.php";

        if (!newattnspinvalue.equals("")) {
            if (!latitude.equals("") && !longitude.equals("")) {
                in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, response -> {
                    if ("success".equals(response)) {
                        Toast.makeText(MarkAttendance.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                        userDetails.AttnMarkedAs = attngetmarked;
                        userDetails.AttnDateTime = attngetdatetime;
                        startActivity(new Intent(MarkAttendance.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MarkAttendance.this, "Error Marking Attendance", Toast.LENGTH_LONG).show();
                    }
                }, error -> Toast.makeText(MarkAttendance.this, error.toString().trim(), Toast.LENGTH_SHORT).show()) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("atten_status", newattnspinvalue);
                        data.put("userid", userDetails.UserId);
                        data.put("location_let", latitude);
                        data.put("location_long", longitude);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            } else {
                errormassage.setText("Please turn on Location");
                Toast.makeText(this, "Please turn on Location", Toast.LENGTH_LONG).show();
            }
        } else {
            errormassage.setText("Please select attendance status");
            Toast.makeText(this, "Please select attendance status or check", Toast.LENGTH_LONG).show();
        }
    }
}

package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeDetails extends AppCompatActivity {

    Spinner spnEmployee;
    TextView txtUserFullName, txtUserCode, txtMobile, txtMobileAlt, txtEmailID, txtDesignation,txtLocation,
            txtEmployeeType, txtDOB, txtDOJ, txtLeavesAllotted, txtPresent, txtAbsent, txtLeavesTaken,
            txtBalanceLeaves, txtEL;
    ArrayList<String> usernames = new ArrayList<>();
    final HashMap<String, String> userMap = new HashMap<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        spnEmployee = findViewById(R.id.spnEmployee);
        txtUserFullName = findViewById(R.id.txtUserFullName);
        txtUserCode = findViewById(R.id.txtUserCode);
        txtMobile = findViewById(R.id.txtMobile);
        txtMobileAlt = findViewById(R.id.txtMobileAlt);
        txtEmailID = findViewById(R.id.txtEmailID);
        txtDesignation = findViewById(R.id.txtDesignation);
        txtLocation = findViewById(R.id.txtLocation);
        txtEmployeeType = findViewById(R.id.txtEmployeeType);
        txtDOB = findViewById(R.id.txtDOB);
        txtDOJ = findViewById(R.id.txtDOJ);
        txtLeavesAllotted = findViewById(R.id.txtLeavesAllotted);
        txtPresent = findViewById(R.id.txtPresent);
        txtAbsent = findViewById(R.id.txtAbsent);
        txtLeavesTaken = findViewById(R.id.txtLeavesTaken);
        txtBalanceLeaves = findViewById(R.id.txtBalanceLeaves);
        txtEL = findViewById(R.id.txtEL);
        requestQueue = volleySingelton.getmInstance(this).getRequestQueue();

        getEmployeeList();

    }
    public void getEmployeeList() {

        final ArrayList<String> employeeIds = new ArrayList<>();
        final ArrayList<String> employeeNames = new ArrayList<>();
        final HashMap<String, String> userMap = new HashMap<>();
        String urlFetch = PublicURL + "getemployeelist.php";
        HttpsTrustManager.allowAllSSL();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlFetch, null,
                new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response == null || response.length() == 0) {
                        Toast.makeText(EmployeeDetails.this, "NO DATA", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Clear any existing data
                    employeeIds.clear();
                    employeeNames.clear();
                    userMap.clear();
                    // Parse JSON response
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String id = jsonObject.getString("userid");
                            String name = jsonObject.getString("username");
                            // Add to collections
                            employeeIds.add(id);
                            employeeNames.add(name);
                            userMap.put(id, name);
                        } catch (Exception e) {
                            Log.e("JSON Error", e.toString());
                        }
                    }
                    // Populate spinner
                    ArrayList<String> userDisplayList = new ArrayList<>();
                    for (int i = 0; i < employeeIds.size(); i++) {
                        userDisplayList.add(employeeIds.get(i) + " - " + employeeNames.get(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EmployeeDetails.this, android.R.layout.simple_spinner_item, userDisplayList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnEmployee.setAdapter(adapter);
                    // Set item selection listener
                    spnEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            String[] splitItem = selectedItem.split(" - ");
                            if (splitItem.length == 2) {
                                String selectedUserid = splitItem[0];
                                String selectedUsername = splitItem[1];
                                Toast.makeText(EmployeeDetails.this, "UserID: " + selectedUserid + "\nUsername: " + selectedUsername, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Handle no selection
                        }
                    });

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(EmployeeDetails.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
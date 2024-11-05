package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaveDetailsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<requestAppliedList> requestAppliedLists; // Ensure this is initialized
    private Button btnRefresh;

    private Spinner tvLeaveStatus;

    String[] leavestatus = {"Applied", "Approved", "Decline", "Cancelled"};

    String stStatus;
    ArrayAdapter leavestatusad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_details_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = volleySingelton.getmInstance(this).getRequestQueue();

        // Initialize the list
        requestAppliedLists = new ArrayList<>(); // Initialize the list to avoid NullPointerException

        fetchData(stStatus);

        btnRefresh = findViewById(R.id.btnReferesh); // Fixed variable name
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData(stStatus); // Fixed method name
            }
        });

        tvLeaveStatus = findViewById(R.id.spStatusSelect);
        leavestatusad  = new ArrayAdapter(LeaveDetailsList.this, android.R.layout.simple_spinner_item, leavestatus);
        leavestatusad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvLeaveStatus.setAdapter(leavestatusad);

        tvLeaveStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stStatus = parent.getItemAtPosition(position).toString();
                fetchData(stStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void fetchData(String stStatus) {

        String stData = stStatus+"|"+UserId;

        String urlFetch = PublicURL + "fatchleavesstatus.php?data="+stData;
        HttpsTrustManager.allowAllSSL();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, urlFetch, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("API Response", response.toString());
                        if (response == null || response.length() == 0) {
                            Toast.makeText(LeaveDetailsList.this, "NO DATA", Toast.LENGTH_LONG).show();
                            return;
                        }
                        requestAppliedLists.clear(); // Clear the list before adding new data
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String employeename = jsonObject.getString("employeename");
                                String leaveapptype = jsonObject.getString("leaveapptype");
                                String leavetype = jsonObject.getString("leavetype");
                                String leaveid = jsonObject.getString("leaveid");
                                String userid = jsonObject.getString("userid");

                                requestAppliedList requestAppliedList = new requestAppliedList(employeename, leaveapptype, leavetype, leaveid, userid);
                                requestAppliedLists.add(requestAppliedList); // Add to the list
                            } catch (Exception e) {
                                Log.e("JSON Error", e.toString());
                            }
                        }

                        // Set the adapter after updating the list
                        requestListAppliedAdaptar requestListAppliedAdapter = new requestListAppliedAdaptar(LeaveDetailsList.this, requestAppliedLists);
                        recyclerView.setAdapter(requestListAppliedAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(LeaveDetailsList.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onBackPressed() { // Fixed method name to override properly
        super.onBackPressed();
        if (requestAppliedLists != null) {
            requestAppliedLists.clear();
        }
        startActivity(new Intent(LeaveDetailsList.this, MainActivity.class));

    }
}
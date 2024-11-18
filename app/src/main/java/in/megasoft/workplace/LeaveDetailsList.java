package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    Spinner spLeaveStatus;

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

        requestAppliedLists = new ArrayList<>();

        fetchData();

        btnRefresh = findViewById(R.id.btnReferesh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });
    }
    private void fetchData() {
        String urlFetch = PublicURL + "fatchleavesstatus.php?data="+UserId;
        HttpsTrustManager.allowAllSSL();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlFetch, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response == null || response.length() == 0) {
                        Toast.makeText(LeaveDetailsList.this, "NO DATA", Toast.LENGTH_LONG).show();
                        return;
                    }
                    requestAppliedLists.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String employeename = jsonObject.getString("employeename");
                            String leaveapptype = jsonObject.getString("leaveapptype");
                            String leavetype = jsonObject.getString("leavetype");
                            String leaveid = jsonObject.getString("leaveid");
                            String userid = jsonObject.getString("userid");
                            String type = jsonObject.getString("type");
                            String status = jsonObject.getString("status");

                            requestAppliedList requestAppliedList = new requestAppliedList(employeename, leaveapptype, leavetype, leaveid, userid, type, status);
                            requestAppliedLists.add(requestAppliedList);

                        } catch (Exception e) {
                            Log.e("JSON Error", e.toString());
                        }
                    }
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
    public void onBackPressed() {
        super.onBackPressed();
        if (requestAppliedLists != null) {
            requestAppliedLists.clear();
        }
        startActivity(new Intent(LeaveDetailsList.this, MainActivity.class));
    }
}
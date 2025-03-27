package in.megasoft.workplace;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InOutTime extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static LocationAdapter adapter;
    private List<LocationTimings> locationTimingsList = new ArrayList<>();
    private RequestQueue requestQueue;
    String stMassage, msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out_time);

        recyclerView = findViewById(R.id.recyclerView);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnCalcel = findViewById(R.id.btnCancle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(InOutTime.this, locationTimingsList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        List<LocationTimings> updatedList = adapter.getUpdatedList();

        fetchLocationData();

        btnUpdate.setOnClickListener(view -> sendUpdatedData(this, updatedList));
        btnCalcel.setOnClickListener(view -> finish());
    }

    private void fetchLocationData() {
        String url = userDetails.URL + "fetch-location";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               try {
                    JSONArray jsonArray = new JSONArray(response);
                    locationTimingsList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        locationTimingsList.add(new LocationTimings(
                                obj.getString("ID"),
                                obj.getString("name"),
                                obj.getString("login_time"),
                                obj.getString("logout_time")
                        ));
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e("PARSE_ERROR", "Error parsing JSON", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                Toast.makeText(InOutTime.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void sendUpdatedData(Context context, List<LocationTimings> updatedList) {

        String url = userDetails.URL + "update-location";

        JSONObject jsonBody = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            for (LocationTimings loc : updatedList) {
                JSONObject obj = new JSONObject();
                obj.put("ID", loc.getId());
                obj.put("login_time", loc.getIntime());
                obj.put("logout_time", loc.getOuttime());
                jsonArray.put(obj);
            }
            jsonBody.put("data", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        String responsebody = response.toString();
                        try {
                            JSONObject jsonResponse = new JSONObject(responsebody);
                            String error = jsonResponse.optString("error", "");
                            msg = jsonResponse.optString("msg", "");

                                if (!msg.equalsIgnoreCase("")) {
                                    stMassage = msg.toString();
                                    showAlertDialog();
                                }else if (!error.equalsIgnoreCase("")) {
                                    stMassage = error.toString();
                                }
                        } catch (Exception e) {
                            Toast.makeText(InOutTime.this, "Response parsing error", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("Volley Error", "Response Code: " + error.networkResponse.statusCode);
                            Log.e("Volley Error", "Response: " + responseBody);
                        } else {
                            Log.e("Volley Error", "No response from server");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer YOUR_TOKEN"); // Add token if required
                return headers;
            }
        };

        // Add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!msg.equals("")){
                    dialog.dismiss();
                    finish();
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
package in.megasoft.workplace;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InOutTime extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<LocationTimings> locationTimingsList = new ArrayList<>();
    private static final String API_URL = userDetails.URL + "fetch-location";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out_time);

        recyclerView = findViewById(R.id.recyclerView);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnCalcel = findViewById(R.id.btnCancle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(locationTimingsList);
        recyclerView.setAdapter(adapter);

//        fetchDepartmentData();
//
//        btnUpdate.setOnClickListener(view -> sendUpdatedData());
        btnCalcel.setOnClickListener(view -> finish());
    }

    private void fetchDepartmentData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Failed to fetch data", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    departmentList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        departmentList.add(new DepartmentTime(
                                obj.getInt("id"),
                                obj.getString("dptname"),
                                obj.getString("intime"),
                                obj.getString("outtime")
                        ));
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } catch (Exception e) {
                    Log.e("PARSE_ERROR", "Error parsing JSON", e);
                }
            }
        });
    }

    private void sendUpdatedData() {
        List<DepartmentTime> updatedList = adapter.getUpdatedList();
        JSONArray jsonArray = new JSONArray();

        for (DepartmentTime dept : updatedList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", dept.getId());
                obj.put("intime", dept.getIntime());
                obj.put("outtime", dept.getOuttime());
                jsonArray.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                jsonArray.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("UPDATE_ERROR", "Failed to update data", e);
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                Log.d("UPDATE_SUCCESS", "Data updated successfully");
            }
        });
    }
}
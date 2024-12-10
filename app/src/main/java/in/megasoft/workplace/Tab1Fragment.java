package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Iterator;
import java.util.List;

public class Tab1Fragment extends Fragment {
    RecyclerView recyclerView;
    Spinner spMonth;
    List<AttendanceData> dataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab1_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        spMonth = rootView.findViewById(R.id.spMonth);

        getDetails();

        return rootView;

    }
    public void getDetails() {
        String url = PublicURL + "fatchattendancedetails.php";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dataList.clear();

                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() == 0) return;

                            JSONObject firstEmployee = jsonArray.getJSONObject(0);
                            List<String> dayKeys = new ArrayList<>();
                            for (Iterator<String> it = firstEmployee.keys(); it.hasNext(); ) {
                                String key = it.next();
                                if (key.startsWith("day")) {
                                    dayKeys.add(key);
                                }
                            }

                            int numberOfColumns = dayKeys.size() + 1;
                            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumns));

                            dataList.add(new AttendanceData("Name/Date"));
                            for (String dayKey : dayKeys) {
                                dataList.add(new AttendanceData(dayKey));
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);
                                dataList.add(new AttendanceData(employee.getString("name")));

                                for (String dayKey : dayKeys) {
                                    dataList.add(new AttendanceData(employee.getString(dayKey)));
                                }
                            }

                            AttendanceAdapter adapter = new AttendanceAdapter(dataList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error parsing response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error occurred", error);
                    }
                });

        requestQueue.add(stringRequest);
    }

}
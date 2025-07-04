package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.URL;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayList extends AppCompatActivity{

    Spinner spnSelectYear;
    ListView lvHolidayList;
    String stYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spnSelectYear = findViewById(R.id.spnSelectYear);
        lvHolidayList = findViewById(R.id.lvHolidayList);

        stYear = "Upcoming";
        List<String> years = getYearsList(2024);
        years.add(0, "Upcoming");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectYear.setAdapter(adapter);

        HolidayListFun();

        spnSelectYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stYear = parent.getItemAtPosition(position).toString();
                HolidayListFun();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private List<String> getYearsList(int startYear) {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = startYear; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        return years;
    }
    private void HolidayListFun(){
        String url = URL + "fetch-holiday-list";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] holidaylist = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                holidaylist[i] = jsonArray.getString(i);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(HolidayList.this, android.R.layout.simple_list_item_1, holidaylist);
                            ListView lvHolidayList = findViewById(R.id.lvHolidayList);
                            lvHolidayList.setAdapter(adapter);
                            setListViewHeightBasedOnChildren(lvHolidayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HolidayList.this, "Error deleting request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("styear", stYear);
                return params;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        requestQueue.add(stringRequest);
    }

    public void setListViewHeightBasedOnChildren(ListView lvHolidayList) {
        ListAdapter listAdapter = lvHolidayList.getAdapter();
        if (listAdapter == null) {
            // Pre-condition
            return;
        }

        int totalHeight = 0;
        int itemCount = listAdapter.getCount();

        for (int i = 0; i < itemCount; i++) {
            View listItem = listAdapter.getView(i, null, lvHolidayList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lvHolidayList.getLayoutParams();
        params.height = totalHeight + (lvHolidayList.getDividerHeight() * (itemCount - 1));
        lvHolidayList.setLayoutParams(params);
        lvHolidayList.requestLayout();
    }
}
package in.megasoft.workplace;

import android.os.Bundle;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DailyWorkDetails extends AppCompatActivity {
    GridView gridView = findViewById(R.id.gridView);

    private final String jsonData = "[{\"NAME\":\"Sandeep Jitonkar\",\"RECIPT\":\"482\",\"RECIPT PENDING\":\"9\",\"GFR\":\"0\",\"GFR PENDING\":\"0\",\"BOOK ADJ\":\"0\",\"BOOK ADJ PENDING\":\"0\",\"ADVANCE PENDING\":\"0\",\"AFORM\":\"16\",\"AFORM PENDING\":\"1\",\"RFORM\":\"0\",\"RFORM PENDING\":\"0\",\"RECONNECTION\":\"0\",\"RECONNECTION PENDING\":\"0\",\"METER REPLACE\":\"0\",\"METER REPLACE PENDING\":\"0\",\"DFORM\":\"0\",\"DFORM PENDING\":\"0\",\"FINAL BILL\":\"0\",\"FINAL BILL PENDING\":\"0\",\"BILL CORRECTION\":\"0\",\"BILL CORRECTION PENDING\":\"0\",\"STATEMENT\":\"67\",\"STATEMENT PENDING\":\"6\"}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_work_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Map<String, String>> data = parseJson(jsonData);
        GridAdapter adapter = new GridAdapter(this, data);
        gridView.setAdapter(adapter);

    }
    private List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> list = new ArrayList<>();
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
        }
        return list;
    }
}
package in.megasoft.workplace;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerMetrics extends AppCompatActivity {
    String[] serverNames = {"MSSiOT", "PHESGOA", "KTC", "MSS"};
    String[] serverUrls = {
        "https://netrasagar.in/sysmonitor/monitor.php",
        "https://mss-util.in/sysmonitor/monitor.php",
        "https://ktc-orc.in/sysmonitor/monitor.php",
        "https://mssgpsdata.in/sysmonitor/monitor.php"
    };

    LinearLayout chartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_metrics);

        chartContainer = findViewById(R.id.chartContainer);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        for (int i = 0; i < serverUrls.length; i++) {
            addServerCharts(serverNames[i], serverUrls[i]);
        }
    }

    void addServerCharts(String serverName, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Scanner sc = new Scanner(conn.getInputStream()).useDelimiter("\\A");
            String result = sc.hasNext() ? sc.next() : "";
            JSONObject obj = new JSONObject(result);

            float cpu = Float.parseFloat(obj.getString("cpu_percent"));
            float mem = Float.parseFloat(obj.getString("memory_percent"));
            float disk = Float.parseFloat(obj.getString("disk_percent"));

            TextView label = new TextView(this);
            label.setText(serverName);
            label.setTextSize(18f);
            label.setTypeface(label.getTypeface(), Typeface.BOLD);
            chartContainer.addView(label);

            chartContainer.addView(makePieChart("CPU Usage", cpu));
            chartContainer.addView(makePieChart("Memory Usage", mem));
            chartContainer.addView(makePieChart("Disk Usage", disk));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PieChart makePieChart(String label, float percent) {
        PieChart chart = new PieChart(this);
        chart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 600));

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percent, "Used"));
        entries.add(new PieEntry(100 - percent, "Free"));

        PieDataSet dataSet = new PieDataSet(entries, label);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.setCenterText(label + "\n" + percent + "%");
        chart.setCenterTextSize(14f);
        chart.invalidate();

        return chart;
    }
}
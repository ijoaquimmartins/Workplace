package in.megasoft.workplace;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ManyDaysPickerActivity extends AppCompatActivity {

    ListView datelist;

    public static ArrayList<String> datearrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.many_days_picker_activity);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        calendarView.setOnForwardPageChangeListener(() ->
                Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show());

        calendarView.setOnPreviousPageChangeListener(() ->
                Toast.makeText(getApplicationContext(), "Previous", Toast.LENGTH_SHORT).show());

        calendarView.setSelectedDates(getSelectedDays());

        List<EventDay> events = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);

    //    events.add(new EventDay(cal, R.drawable.sample_four_icons));

        calendarView.setEvents(events);

        Button getDateButton = (Button) findViewById(R.id.getDateButton);

        getDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (java.util.Calendar calendar : calendarView.getSelectedDates()) {
                    System.out.println(calendar.getTime().toString());

                    Toast.makeText(getApplicationContext(),
                            calendar.getTime().toString(),
                            Toast.LENGTH_LONG).show();

                    getSelectedDays();

                }
            }
        });

    }

    public ArrayList<java.util.Calendar> getSelectedDays() {
        ArrayList<java.util.Calendar> calendars = new ArrayList<>();

        for (int i = 0; i < 20; i++) {

            java.util.Calendar calendar = DateUtils.getCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            calendars.add(calendar);

        }
        return calendars;
    }
}
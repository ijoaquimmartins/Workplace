package in.megasoft.workplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<AttendanceItem> {
    private Context context;
    private List<AttendanceItem> attendanceList;

    public AttendanceAdapter(Context context, List<AttendanceItem> attendanceList) {
        super(context, R.layout.list_item, attendanceList);
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvItem);
        AttendanceItem item = attendanceList.get(position);

        // Set text
        textView.setText(item.getText());

        // Change background color based on status
        if (item.getStatus() == '1') {
            //convertView.setBackgroundColor(Color.RED);
            textView.setTextColor(ContextCompat.getColor(context, R.color.darkred));
        } else if (item.getStatus() == '0') {
            //convertView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(ContextCompat.getColor(context, R.color.darkgreen));
        }

        return convertView;
    }
}

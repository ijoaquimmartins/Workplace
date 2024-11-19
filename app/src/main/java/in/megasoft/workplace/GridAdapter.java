package in.megasoft.workplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class GridAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map<String, String>> data;

    public GridAdapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewDetails = convertView.findViewById(R.id.textViewDetails);

        // Get current item
        Map<String, String> currentItem = data.get(position);

        // Set data
        textViewName.setText(currentItem.get("NAME"));
        String details = "RECIPT: " + currentItem.get("RECIPT") + "\n" +
                "AFORM: " + currentItem.get("AFORM") + "\n" +
                "STATEMENT: " + currentItem.get("STATEMENT");
        textViewDetails.setText(details);

        return convertView;
    }
}

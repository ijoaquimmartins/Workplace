package in.megasoft.workplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaveAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<LeaveData> leaveDataList;

    public LeaveAdapter(Context context, List<LeaveData> leaveDataList) {
        this.context = context;
        this.leaveDataList = leaveDataList;
    }

    @Override
    public int getGroupCount() {
        return leaveDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // Split the comma-separated leave dates into individual names for each group
        return leaveDataList.get(groupPosition).getLeaveDates().split(",").length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return leaveDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // Split the comma-separated leave dates into an array and return the child name
        return leaveDataList.get(groupPosition).getLeaveDates().split(",")[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        LeaveData group = (LeaveData) getGroup(groupPosition);
        TextView groupText = convertView.findViewById(android.R.id.text1);
        groupText.setText(group.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String child = (String) getChild(groupPosition, childPosition);
        TextView childText = convertView.findViewById(android.R.id.text1);
        childText.setText(child);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


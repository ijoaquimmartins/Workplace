package in.megasoft.workplace;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<LocationTimings> locationList;
    public LocationAdapter(List<LocationTimings> locationList){
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timings_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        LocationTimings department = locationList.get(position);
        holder.tvLocation.setText(department.getname());
        holder.etInTime.setText(department.getIntime());
        holder.etOutTime.setText(department.getOuttime());

        // Update model on text change
        holder.etInTime.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                department.setIntime(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        holder.etOutTime.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                department.setOuttime(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;
        EditText etInTime, etOutTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLocation = itemView.findViewById(R.id.tvLocation);
            etInTime = itemView.findViewById(R.id.etInTime);
            etOutTime = itemView.findViewById(R.id.etOutTime);

        }
    }
}

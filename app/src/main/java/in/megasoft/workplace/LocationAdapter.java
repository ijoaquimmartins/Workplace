package in.megasoft.workplace;

import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<LocationTimings> locationList;
    private final Context context;
    String onclick="";
    public LocationAdapter(Context context, List<LocationTimings> locationList){
        this.context = context;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timings_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationTimings department = locationList.get(position);
        holder.tvLocation.setText(department.getname());
        holder.etInTime.setText(department.getIntime());
        holder.etOutTime.setText(department.getOuttime());

        holder.etInTime.setOnClickListener(view -> {
            onclick = "1";
            showSpinnerTimePickerDialog(holder, position);
        });

        holder.etOutTime.setOnClickListener(view -> {
            onclick = "2";
            showSpinnerTimePickerDialog(holder, position);
        });

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
        return locationList.size();
    }

    public List<LocationTimings> getUpdatedList() {
        return locationList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation, etInTime, etOutTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLocation = itemView.findViewById(R.id.tvLocation);
            etInTime = itemView.findViewById(R.id.etInTime);
            etOutTime = itemView.findViewById(R.id.etOutTime);

        }
    }
    private void showSpinnerTimePickerDialog(ViewHolder holder, int position) {

        if (context == null) return;

        int hour = 0, minute = 0;

        String stInTime = holder.etInTime.getText().toString();
        String stOutTime = holder.etOutTime.getText().toString();
        if(onclick.equals("1")){
            if (!stInTime.equals("Select Time")) {
                String[] parts = stInTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } else {
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }
        } else if (onclick.equals("2")) {
            if (!stOutTime.equals("Select Time")) {
                String[] parts = stOutTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } else {
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }
        }

        // Set the spinner theme for TimePickerDialog
        int spinnerTheme = android.R.style.Theme_Holo_Light_Dialog_NoActionBar;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                spinnerTheme,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                //    timeList.set(position, selectedTime);
                    notifyItemChanged(position);
                    if(onclick.equals("1")){
                        holder.etInTime.setText(selectedTime+":00");
                    } else if (onclick.equals("2")) {
                        holder.etOutTime.setText(selectedTime+":00");
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }
}

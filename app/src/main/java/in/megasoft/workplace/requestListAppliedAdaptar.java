package in.megasoft.workplace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class requestListAppliedAdaptar extends RecyclerView.Adapter<requestListAppliedAdaptar.requestHolder> {

    private Context context;
    private List<requestAppliedList> requestAppliedlist;

    public requestListAppliedAdaptar(Context context, List<requestAppliedList> requestAppliedlists) {
        this.context = context;
        this.requestAppliedlist = requestAppliedlists; // Added 'this' for clarity
    }

    @NonNull
    @Override
    public requestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_view, parent, false);
        return new requestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull requestHolder holder, int position) {
        requestAppliedList requestAppliedList = requestAppliedlist.get(position);
        holder.employee.setText(requestAppliedList.getUserL());
        holder.leaveapptype.setText(requestAppliedList.getLeaveapptypeL());
        holder.leavetype.setText(requestAppliedList.getLeavetypeL());
        holder.leaveid.setText(requestAppliedList.getLeaveidL());
        holder.userid.setText(requestAppliedList.getUseridL());

        String stEmployee;

        stEmployee = requestAppliedList.getUserL();

        if(stEmployee.equals("No Leaves Applied")){
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
        }else {

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ApproveLeave.class); // Use the correct activity class
                    Bundle bundle = new Bundle();
                    bundle.putString("employeename", requestAppliedList.getUserL());
                    bundle.putString("leaveappid", requestAppliedList.getLeaveapptypeL());
                    bundle.putString("leavetype", requestAppliedList.getLeavetypeL());
                    bundle.putString("leaveid", requestAppliedList.getLeaveidL());
                    bundle.putString("userid", requestAppliedList.getUseridL());
                    intent.putExtras(bundle); // Add this line to pass the bundle
                    context.startActivity(intent); // Start the activity
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestAppliedlist.size();
    }

    public class requestHolder extends RecyclerView.ViewHolder {
        TextView employee, leaveapptype, leavetype, leaveid, userid;
        RelativeLayout relativeLayout;

        public requestHolder(@NonNull View itemView) {
            super(itemView);
            employee = itemView.findViewById(R.id.tvEmployee);
            leaveapptype = itemView.findViewById(R.id.tvLeaveAppType);
            leavetype = itemView.findViewById(R.id.tvLeavetype);
            leaveid = itemView.findViewById(R.id.tvLeaveId);
            userid = itemView.findViewById(R.id.tvUserId);
            relativeLayout = itemView.findViewById(R.id.list_view);


        }
    }
}

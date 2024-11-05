package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Functions {

    private RequestQueue requestQueue, request;
    private Context context;

    public Functions(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        request = Volley.newRequestQueue(context);
    }

    public void getUserData() {
        String url = userDetails.PublicURL + "getuserdetails.php?user_name=" + userDetails.UserName;
        HttpsTrustManager.allowAllSSL(); // Ensure this is used judiciously

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    UserId = jsonObject.getString("userid");
                    userDetails.UserName = jsonObject.getString("username");
                    userDetails.UserFullName = jsonObject.getString("name");
                    userDetails.EmailID = jsonObject.getString("emailid");
                    userDetails.MobileNo = jsonObject.getString("mobileno");
                    userDetails.PicPath = jsonObject.isNull("picpath") ? null : jsonObject.getString("picpath");
                    userDetails.AttnMarkedAs = jsonObject.getString("atnstatus");
                    userDetails.AttnDateTime = jsonObject.getString("atndate");

                    // Example toast (make sure it's on the UI thread if needed)
                    Toast.makeText(context, "Welcome " + userDetails.UserFullName, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getusermoduledata(){

        String userid = UserId;

        String cntUrl = PublicURL + "getmodule.php?username=" + userDetails.UserName;

        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, cntUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] data = response.split("#");

                if(Arrays.asList(data).contains("AttendanceMark")){
                    userDetails.AttendanceMark="1";
                }else {
                    userDetails.AttendanceMark="2";
                }

                if(Arrays.asList(data).contains("LeaveApplication")){
                    userDetails.LeaveApplication="1";
                }else {
                    userDetails.LeaveApplication="2";
                }
                if(Arrays.asList(data).contains("DailyWork")){
                    userDetails.DailyWork="1";
                }else {
                    userDetails.DailyWork="2";
                }
                if(Arrays.asList(data).contains("EmployeeDetails")){
                    userDetails.EmployeeDetails="1";
                }else {
                    userDetails.EmployeeDetails="2";
                }
                if(Arrays.asList(data).contains("HolidayDetails")){
                    userDetails.HolidayDetails="1";
                }else {
                    userDetails.HolidayDetails="2";
                }
                if(Arrays.asList(data).contains("TotalLeave")){
                    userDetails.TotalLeave="1";
                }else {
                    userDetails.TotalLeave="2";
                }
                if(Arrays.asList(data).contains("ApproveLeave")){
                    userDetails.ApproveLeave="1";
                }else {
                    userDetails.ApproveLeave="2";
                }
                if(Arrays.asList(data).contains("AttendanceDetails")){
                    userDetails.AttendanceDetails="1";
                }else {
                    userDetails.AttendanceDetails="2";
                }
                if(Arrays.asList(data).contains("LeaveDetails")){
                    userDetails.LeaveDetails="1";
                }else {
                    userDetails.LeaveDetails="2";
                }
                if(Arrays.asList(data).contains("DailyWorkDetails")){
                    userDetails.DailyWorkDetails="1";
                }else {
                    userDetails.DailyWorkDetails="2";
                }

                // Toast.makeText(FullscreenActivity.this, "Welcome  "+data[0], Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }


}

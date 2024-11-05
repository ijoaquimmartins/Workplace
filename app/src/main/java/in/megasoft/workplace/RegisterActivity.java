package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView reg_deviceid;
    EditText  reg_firstname, reg_middlename, reg_lastname, reg_mobile,
            reg_altmobile, reg_dob, reg_doj, reg_password, reg_confpassword;
    Button reg_submit, reg_cancel;
    DatePickerDialog picker;
    String dateofbirth, dateofjoining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg_deviceid = findViewById(R.id.txtdeviceId);
        reg_firstname = findViewById(R.id.txtEmployeeFirstName);
        reg_middlename = findViewById(R.id.txtEmployeeMiddleName);
        reg_lastname = findViewById(R.id.txtEmployeeLastName);
        reg_mobile = findViewById(R.id.txtMobileNo);
        reg_altmobile = findViewById(R.id.txtAltmobileno);
        reg_dob = findViewById(R.id.txtregisterDob);
        reg_doj = findViewById(R.id.txtregisterDoj);
        reg_password = findViewById(R.id.txtregpassword);
        reg_confpassword = findViewById(R.id.txtregconpassword);
        reg_submit = findViewById(R.id.btnregsubmit);
        reg_cancel = findViewById(R.id.btnregcancel);

        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        reg_deviceid.setText(mId);

        reg_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                reg_dob.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                dateofbirth = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });

        reg_doj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year1, int monthOfYear1, int dayOfMonth1) {
                                reg_doj.setText(dayOfMonth1 + "/" + (monthOfYear1 + 1) + "/" + year1);
                                dateofjoining = (year1 + "-" + (monthOfYear1 + 1) + "-" + dayOfMonth1);
                            }
                        }, year1, month1, day1);
                picker.show();
            }
        });

        reg_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        reg_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void register(){

        String reg_deviceid1 = reg_deviceid.getText().toString();
        String reg_firstname1 = reg_firstname.getText().toString();
        String reg_middlename1 = reg_middlename.getText().toString();
        String reg_lastname1 = reg_lastname.getText().toString();
        String reg_mobile1 = reg_mobile.getText().toString();
        String reg_altmobile1 = reg_altmobile.getText().toString();
        String reg_dob1 = reg_dob.getText().toString();
        String reg_doj1 = reg_doj.getText().toString();
        String reg_password1 = reg_password.getText().toString();
        String reg_confpassword1 = reg_confpassword.getText().toString();

        String urlsubmit = PublicURL + "register.php";

        if(!reg_firstname.equals("") && !reg_lastname.equals("") && !reg_mobile.equals("") &&
                !reg_dob.equals("") && !reg_doj.equals("") && !reg_password.equals("") && !reg_confpassword.equals("")){
            if(reg_password1.equals(reg_confpassword1)){

                in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlsubmit, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.equals("success")){

                                Toast.makeText(RegisterActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (response.equals("failure")) {
                                Toast.makeText(RegisterActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> data = new HashMap<>();

                            data.put("reg_deviceid", reg_deviceid1);
                            data.put("reg_firstname", reg_firstname1);
                            data.put("reg_middlename", reg_middlename1);
                            data.put("reg_lastname", reg_lastname1);
                            data.put("reg_mobile", reg_mobile1);
                            data.put("reg_altmobile", reg_altmobile1);
                            data.put("reg_dob", reg_dob1);
                            data.put("reg_doj", reg_doj1);
                            data.put("reg_password", reg_password1);

                            return data;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,3,1.0f));
                    requestQueue.add(stringRequest);

            }else{
                Toast.makeText(this, "Password and confirm password dose not match ", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, R.string.compulsory, Toast.LENGTH_LONG).show();
        }


    }
}
package in.megasoft.workplace;

import static in.megasoft.workplace.userDetails.PublicURL;
import static in.megasoft.workplace.userDetails.UserId;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DailyWork extends AppCompatActivity {

    EditText etDeptRecieptCmplt, etAformCmplt, etRformCmplt, etDformCmplt, etFinalBillCmplt, etAdvanceCmplt, etReconnectionCmplt, etBillcorrectionCmplt, etMeterRepCmplt, etgfrCmplt, etStatementCmplt, etBookAdjstCmplt, etDeptRecieptPndg, etAformPndg, etRformPndg, etDformPndg, etFinalBillPndg, etAdvancePndg, etReconnectionPndg, etBillcorrectionPndg, etMeterRepPndg, etgfrPndg, etStatementPndg, etBookAdjstPndg, etRemark;
    CheckBox cbDataUploaded;
    Button btnDailyWorkDataSub, btnDailyWorkDataCan;
    String dataUploaded, rowId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_work);

        etDeptRecieptCmplt = findViewById(R.id.tvDpetRectDone);
        etAformCmplt = findViewById(R.id.tvaFormDone);
        etRformCmplt = findViewById(R.id.tvrFormDone);
        etDformCmplt = findViewById(R.id.tvdFormDone);
        etFinalBillCmplt = findViewById(R.id.tvFinalBillDone);
        etAdvanceCmplt = findViewById(R.id.tvAdvanceDone);
        etReconnectionCmplt = findViewById(R.id.tvRconnDone);
        etBillcorrectionCmplt = findViewById(R.id.tvBillCorDone);
        etMeterRepCmplt = findViewById(R.id.tvMetRepDone);
        etgfrCmplt = findViewById(R.id.tvGFRDone);
        etStatementCmplt = findViewById(R.id.tvStatementDone);
        etBookAdjstCmplt = findViewById(R.id.tvBookAdjDone);
        etDeptRecieptPndg = findViewById(R.id.tvDpetRectPen);
        etAformPndg = findViewById(R.id.tvaFormPen);
        etRformPndg = findViewById(R.id.tvrFormpen);
        etDformPndg = findViewById(R.id.tvdFormPen);
        etFinalBillPndg = findViewById(R.id.tvFinalBillPen);
        etAdvancePndg = findViewById(R.id.tvAdvancePen);
        etReconnectionPndg = findViewById(R.id.tvRconnPen);
        etBillcorrectionPndg = findViewById(R.id.tvBillCorPen);
        etMeterRepPndg = findViewById(R.id.tvMetRepPen);
        etgfrPndg = findViewById(R.id.tvGFRPen);
        etStatementPndg = findViewById(R.id.tvStatementPen);
        etBookAdjstPndg = findViewById(R.id.tvBookAdjPen);
        etRemark = findViewById(R.id.tvRemark);
        cbDataUploaded = findViewById(R.id.cbDataUpload);
        btnDailyWorkDataSub = findViewById(R.id.btnDailyWorkDataSub);
        btnDailyWorkDataCan = findViewById(R.id.btnDailyWorkDataCan);

        getWorkDetails();

        btnDailyWorkDataSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   dailyWorkSubmit();
            }
        });
        btnDailyWorkDataCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(DailyWork.this, MainActivity.class);
 //               startActivity(i);
                DailyWork.this.finish();
            }
        });
    }
    private void getWorkDetails(){
        String url = PublicURL + "getworkdetails.php?userid=" + UserId;
        RequestQueue request = Volley.newRequestQueue(this);
        in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String jsonString = response.toString();
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    rowId = jsonObject.getString("id");
                    etDeptRecieptCmplt.setText(jsonObject.getString("etDeptRecieptCmplt"));
                    etAformCmplt.setText(jsonObject.getString("etAformCmplt"));
                    etRformCmplt.setText(jsonObject.getString("etRformCmplt"));
                    etDformCmplt.setText(jsonObject.getString("etDformCmplt"));
                    etFinalBillCmplt.setText(jsonObject.getString("etFinalBillCmplt"));
                    etAdvanceCmplt.setText(jsonObject.getString("etAdvanceCmplt"));
                    etReconnectionCmplt.setText(jsonObject.getString("etReconnectionCmplt"));
                    etBillcorrectionCmplt.setText(jsonObject.getString("etBillcorrectionCmplt"));
                    etMeterRepCmplt.setText(jsonObject.getString("etMeterRepCmplt"));
                    etgfrCmplt.setText(jsonObject.getString("etgfrCmplt"));
                    etStatementCmplt.setText(jsonObject.getString("etStatementCmplt"));
                    etBookAdjstCmplt.setText(jsonObject.getString("etBookAdjstCmplt"));
                    etDeptRecieptPndg.setText(jsonObject.getString("etDeptRecieptPndg"));
                    etAformPndg.setText(jsonObject.getString("etAformPndg"));
                    etRformPndg.setText(jsonObject.getString("etRformPndg"));
                    etDformPndg.setText(jsonObject.getString("etDformPndg"));
                    etFinalBillPndg.setText(jsonObject.getString("etFinalBillPndg"));
                    etAdvancePndg.setText(jsonObject.getString("etAdvancePndg"));
                    etReconnectionPndg.setText(jsonObject.getString("etReconnectionPndg"));
                    etBillcorrectionPndg.setText(jsonObject.getString("etBillcorrectionPndg"));
                    etMeterRepPndg.setText(jsonObject.getString("etMeterRepPndg"));
                    etgfrPndg.setText(jsonObject.getString("etgfrPndg"));
                    etStatementPndg.setText(jsonObject.getString("etStatementPndg"));
                    etBookAdjstPndg.setText(jsonObject.getString("etBookAdjstPndg"));
                    etRemark.setText(jsonObject.getString("etRemark"));
                    dataUploaded = jsonObject.getString("data_uploaded");
                    if(dataUploaded.equals("1")){
                        cbDataUploaded.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.add(stringRequest);
    }
    public void dailyWorkSubmit(){

        String etDeptRecieptCmplt1 = etDeptRecieptCmplt.getText().toString();
        String etAformCmplt1 = etAformCmplt.getText().toString();
        String etRformCmplt1 = etRformCmplt.getText().toString();
        String etDformCmplt1 = etDformCmplt.getText().toString();
        String etFinalBillCmplt1 = etFinalBillCmplt.getText().toString();
        String etAdvanceCmplt1 = etAdvanceCmplt.getText().toString();
        String etReconnectionCmplt1 = etReconnectionCmplt.getText().toString();
        String etBillcorrectionCmplt1 = etBillcorrectionCmplt.getText().toString();
        String etMeterRepCmplt1 = etMeterRepCmplt.getText().toString();
        String etgfrCmplt1 = etgfrCmplt.getText().toString();
        String etStatementCmplt1 = etStatementCmplt.getText().toString();
        String etBookAdjstCmplt1 = etBookAdjstCmplt.getText().toString();
        String etDeptRecieptPndg1 = etDeptRecieptPndg.getText().toString();
        String etAformPndg1 = etAformPndg.getText().toString();
        String etRformPndg1 = etRformPndg.getText().toString();
        String etDformPndg1 = etDformPndg.getText().toString();
        String etFinalBillPndg1 = etFinalBillPndg.getText().toString();
        String etAdvancePndg1 = etAdvancePndg.getText().toString();
        String etReconnectionPndg1 = etReconnectionPndg.getText().toString();
        String etBillcorrectionPndg1 = etBillcorrectionPndg.getText().toString();
        String etMeterRepPndg1 = etMeterRepPndg.getText().toString();
        String etgfrPndg1 = etgfrPndg.getText().toString();
        String etStatementPndg1 = etStatementPndg.getText().toString();
        String etBookAdjstPndg1 = etBookAdjstPndg.getText().toString();
        String etRemark1 = etRemark.getText().toString();
        String cbDataUploaded1;
        String username = userDetails.UserId.toString();

        if(cbDataUploaded.isChecked()){
            cbDataUploaded1 = "1";
        }else {
            cbDataUploaded1 = "0";
        }
        String dailyworkdata = etDeptRecieptCmplt1  +  "|"  + etAformCmplt1  +  "|"  + etRformCmplt1  +  "|"  + etDformCmplt1  +  "|"  + etFinalBillCmplt1  +  "|"  + etAdvanceCmplt1  +  "|"  + etReconnectionCmplt1  +  "|"  + etBillcorrectionCmplt1  +  "|"  + etMeterRepCmplt1  +  "|"  + etgfrCmplt1  +  "|"  + etStatementCmplt1  +  "|"  + etBookAdjstCmplt1  +  "|"  + etDeptRecieptPndg1  +  "|"  + etAformPndg1  +  "|"  + etRformPndg1  +  "|"  + etDformPndg1  +  "|"  + etFinalBillPndg1  +  "|"  + etAdvancePndg1  +  "|"  + etReconnectionPndg1  +  "|"  + etBillcorrectionPndg1  +  "|"  + etMeterRepPndg1  +  "|"  + etgfrPndg1  +  "|"  + etStatementPndg1  +  "|"  + etBookAdjstPndg1  +  "|"  + etRemark1  +  "|"  + cbDataUploaded1;
        String dailyworkurl = PublicURL + "adddailywork.php";

        if(!dailyworkdata.equals("")){
            in.megasoft.workplace.HttpsTrustManager.allowAllSSL();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, dailyworkurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.equals("success")){
//                        Intent intent = new Intent(DailyWork.this, MainActivity.class);
//                        startActivity(intent);
                        DailyWork.this.finish();
                    } else if (response.equals("failure")) {
                        Toast.makeText(DailyWork.this, "Error Adding Request", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DailyWork.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("rowid", rowId);
                    data.put("username", username);
                    data.put("dailyworkdata", dailyworkdata);

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,3,1.0f));
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
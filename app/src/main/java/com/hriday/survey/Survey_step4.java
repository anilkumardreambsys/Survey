package com.hriday.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Survey_step4 extends AppCompatActivity {

    Button next4step_btn;
    EditText remarks_val;
    SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_step4);


        remarks_val=findViewById(R.id.remarks1);


        next4step_btn=findViewById(R.id.next4step_btn);
        next4step_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String remarks=remarks_val.getText().toString().trim();

                String survey_insert="https://dreambsys.in/codeigniter/hriday/api/surveyor/updatesstep4_survey";

                StringRequest stringRequest=new StringRequest(Request.Method.POST, survey_insert, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        surveyform_step4(response);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Survey_step4.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error",error.toString()+"");
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String,String> params=new HashMap<String,String>();

                        remarks_val=findViewById(R.id.remarks1);
                        String remarksval=remarks_val.getText().toString();
                        SharedPreferences survey_formid=getSharedPreferences("surveyform", MODE_PRIVATE);
                        String survey_id = survey_formid.getString("surveyform_id","");
                        params.put("remarksstap4",remarksval);
                        params.put("survey_id",survey_id);

                        //params.put("address","sdsdsd");
                        return params;

                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(Survey_step4.this);
                requestQueue.add(stringRequest);
            }

        });
    }

    private void surveyform_step4(String response) {
        // Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show();
        try {
            //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(response);
            Log.d("testquery",""+jsonObject);
            if (jsonObject.getString("status").equals("true")) {

               /* new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Your survey completed")
                        .setContentText("You have done with the Survey information.")
                        .show();*/

                SharedPreferences settings = getSharedPreferences("surveyform", MODE_PRIVATE);
                settings.edit().clear().commit();

                Intent intent = new Intent(Survey_step4.this, Person2person_view.class);
                startActivity(intent);
                Survey_step4.this.finish();

                Toast.makeText(Survey_step4.this, "Surey information has been updated successfully.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Survey_step4.this, "There is an error. Please contact to admin.", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(SurveyForm.this, "TEST", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
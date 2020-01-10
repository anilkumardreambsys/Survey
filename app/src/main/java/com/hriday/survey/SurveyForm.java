package com.hriday.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SurveyForm extends AppCompatActivity {

    Button nextbtn;
    EditText survey_id, dateselected, survey_title,cust_age ,addressline1,addressline2,area_locality,city,city2,state,before_inde,after_inde,years;
    private int mYear, mMonth, mDay, mHour, mMinute;
    RadioGroup gender_grp;
    RadioButton rdb_male,rdb_female;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form);
        nextbtn=findViewById(R.id.next_stepbtn1);

        survey_id=findViewById(R.id.surveyid);

        //survey_id.setText("2");


        Calendar todaydate=Calendar.getInstance();
        mYear=todaydate.get(Calendar.YEAR);
        mMonth =todaydate.get(Calendar.MONTH);
        mDay =todaydate.get(Calendar.DAY_OF_MONTH);
        dateselected=findViewById(R.id.surveydate);
        dateselected.setText(mDay+"-"+(mMonth+1)+"-"+mYear);
        final String survey_date=dateselected.getText().toString(); // Survey Date

        gender_grp=findViewById(R.id.radiogrp_gender);
        get_surveyid();

        final String survey_form_id=survey_id.getText().toString();  //Survey Form id
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                survey_title=findViewById(R.id.survey_title);
                final String s_title=survey_title.getText().toString();   // Survey Title

                cust_age=findViewById(R.id.cust_age);
                final String customerage=cust_age.getText().toString();   // Customer Age

                addressline1=findViewById(R.id.address1);
                final String address_line1=addressline1.getText().toString();  // Address Line 1

                addressline2=findViewById(R.id.address2);
                final String address_line2=addressline2.getText().toString();  // Address Line 2

                area_locality=findViewById(R.id.area_locality);
                final String area_local=area_locality.getText().toString();    // Area/Locality

                city=findViewById(R.id.city);
                final String surveycity=city.getText().toString();    // City

                city2=findViewById(R.id.city2);
                final String surveycity2=city2.getText().toString();    // City2

                state=findViewById(R.id.state);
                final String surveystate=state.getText().toString();    // State

                before_inde  =findViewById(R.id.amtr_input);
                final String before_independence=before_inde.getText().toString(); // Before Independence

                after_inde  =findViewById(R.id.after_independence);
                final String after_independence=after_inde.getText().toString(); // After Independence

                years  =findViewById(R.id.no_of_years);
                final String no_of_years=years.getText().toString(); // No of years


                /*bundle.putString("survey_form_id",survey_form_id);
                bundle.putString("survey_date",survey_date);
                bundle.putString("survey_title",s_title);
                RadioButton rb_male=(RadioButton)findViewById(R.id.rd_male);   // Male Status
                RadioButton rb_female=(RadioButton)findViewById(R.id.rd_female); // Female status
                if(rb_male.getText().toString().equalsIgnoreCase("Male")){
                    bundle.putString("genderstatus","m");
                }else{
                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                    bundle.putString("genderstatus","f");
                }
                bundle.putString("customer_age",customerage);
                bundle.putString("address_line1",address_line1);
                bundle.putString("address_line2",address_line2);
                bundle.putString("area_local",area_local);
                bundle.putString("city",surveycity);
                bundle.putString("city2",surveycity2);
                bundle.putString("state",surveystate);
                bundle.putString("before_independence",before_independence);
                bundle.putString("after_independence",after_independence);
                bundle.putString("years",no_of_years);*/

                new SweetAlertDialog(SurveyForm.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to change this!")
                        .setCancelText("Cancel")
                        .setConfirmText("Yes, Confirmed!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {


                                // Post data to database
                                // Toast.makeText(SurveyForm.this, "Welcome", Toast.LENGTH_SHORT).show();
                                String survey_insert="https://dreambsys.in/codeigniter/hriday/api/surveyor/insert_survey";
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, survey_insert, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Toast.makeText(SurveyForm.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        surveyform_add(response);
                                        //pDialog.hide();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(SurveyForm.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("Error",error.toString()+"");
                                    }
                                }){
                                    protected Map<String, String> getParams(){
                                        Map<String,String> params=new HashMap<String,String>();
                                           /* Intent redirect=new Intent(SurveyForm.this, Survey_step1.class);
                                            Bundle bundle = new Bundle();
                                            try {*/
                                        RadioButton rb_male=(RadioButton)findViewById(R.id.rd_male);   // Male Status
                                        RadioButton rb_female=(RadioButton)findViewById(R.id.rd_female); // Female status
                                                /*JSONObject assignjson_obj = new JSONObject();
                                                JSONArray jsonarray_data = new JSONArray();
                                                assignjson_obj.put("survey_form_id",survey_form_id);
                                                assignjson_obj.put("survey_date",survey_date);
                                                assignjson_obj.put("survey_title",s_title);
                                                RadioButton rb_male=(RadioButton)findViewById(R.id.rd_male);   // Male Status
                                                RadioButton rb_female=(RadioButton)findViewById(R.id.rd_female); // Female status
                                                if(rb_male.getText().toString().equalsIgnoreCase("Male")){
                                                    assignjson_obj.put("genderstatus","m");
                                                }else{
                                                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                                                    assignjson_obj.put("genderstatus","f");
                                                }
                                                assignjson_obj.put("customer_age",customerage);
                                                assignjson_obj.put("address_line1",address_line1);
                                                assignjson_obj.put("address_line2",address_line2);
                                                assignjson_obj.put("area_local",area_local);
                                                assignjson_obj.put("surveycity",surveycity);
                                                assignjson_obj.put("surveycity2",surveycity2);
                                                assignjson_obj.put("surveystate",surveystate);
                                                assignjson_obj.put("before_independence",before_independence);
                                                assignjson_obj.put("after_independence",after_independence);
                                                assignjson_obj.put("years",no_of_years);

                                                jsonarray_data.put(assignjson_obj);  // all json object to array.
                                                bundle.putString("survey_data",jsonarray_data.toString());
                                                Log.d("dataform",""+jsonarray_data.toString());
                                                redirect.putExtras(bundle);*/
                                        SharedPreferences checklogin=getSharedPreferences("user_details", MODE_PRIVATE);
                                        String user_id = checklogin.getString("user_id","");
                                        params.put("survey_number","1");
                                        params.put("user_id",user_id);
                                        params.put("survey_date",survey_date);
                                        params.put("customer_name",s_title);
                                        params.put("address",address_line1);
                                        if(rb_male.getText().toString().equalsIgnoreCase("Male")){
                                            // assignjson_obj.put("genderstatus","m");
                                            params.put("gender","m");
                                        }else{
                                            //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                                            // assignjson_obj.put("genderstatus","f");
                                            params.put("gender","f");
                                        }
                                        params.put("age",customerage);
                                        params.put("city",surveycity);
                                        params.put("city2",surveycity2);
                                        params.put("state",surveystate);
                                        params.put("area",area_local);
                                        params.put("staying_amritsar_before_independence",before_independence);
                                        params.put("staying_amritsar_after_independence",after_independence);
                                        params.put("years_staying_in_amritsar_after_independence",no_of_years);
                                            /*} catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue= Volley.newRequestQueue(SurveyForm.this);
                                requestQueue.add(stringRequest);
                                //end here

                                //startActivity(redirect);



                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

            private void surveyform_add(String response) {
                try {
                    //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("true")) {
                        Intent intent = new Intent(SurveyForm.this, Survey_step1.class);
                        String surveyid = jsonObject.getString("survey_id");

                        SharedPreferences.Editor editor=getSharedPreferences("surveyform",MODE_PRIVATE).edit();
                        editor.putString("surveyform_id",surveyid);
                        editor.putString("alldetails",response.toString());
                        editor.apply();

                        startActivity(intent);

                        //Log.d("surveyid",""+surveyid);
                        Toast.makeText(SurveyForm.this, "Data has been added successfully.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SurveyForm.this, "There is an error. Please contact to admin.", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(SurveyForm.this, "TEST", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });

        dateselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                mYear=c.get(Calendar.YEAR);
                mMonth =c.get(Calendar.MONTH);
                mDay =c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog= new DatePickerDialog(SurveyForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateselected.setText(dayOfMonth+"-"+(month + 1)+"-"+year);
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });

    }

    private void get_surveyid() {
        String survey_id_get="https://dreambsys.in/codeigniter/hriday/api/surveyor/get_survey_id";
        StringRequest stringRequest=new StringRequest(Request.Method.GET, survey_id_get, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d("Res",""+response.toString());
                try {
                    //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.d("Response", "" + response.toString());
                    if (jsonObject.getString("status").equals("true")) {
                        String survey_id_info = jsonObject.getString("data");
                        survey_id.setText(survey_id_info);
                        //Toast.makeText(SurveyForm.this, survey_id_info, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SurveyForm.this, "There is an server error. Please contact to admin", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(SurveyForm.this, "Testing.", Toast.LENGTH_SHORT).show();
                // parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SurveyForm.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Error",error.toString()+"");
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(SurveyForm.this);
        requestQueue.add(stringRequest);

    }
}

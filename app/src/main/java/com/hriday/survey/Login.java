package com.hriday.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class Login extends AppCompatActivity {

    EditText user_id,userpwd;
    Button login_btn;
    String datauser_id, emailid,fname;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_id=findViewById(R.id.loginid);
        userpwd=findViewById(R.id.userpassword);
        login_btn=findViewById(R.id.lgn_btn);

        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        // Check if already login or not
        SharedPreferences checklogin=getSharedPreferences("user_details", MODE_PRIVATE);
        Boolean islogin = checklogin.getBoolean("is_login", false);
        //Toast.makeText(this, ""+islogin, Toast.LENGTH_SHORT).show();
        if(islogin){
            Intent intent=new Intent(this,Person2person_view.class);
            startActivity(intent);
            Login.this.finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(MainActivity.this, "Hello Testing", Toast.LENGTH_SHORT).show();
                if(user_id.getText().toString().length()==0){
                    user_id.requestFocus();
                    Toast.makeText(Login.this, "Please enter your user id.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userpwd.getText().toString().length()==0){
                    userpwd.requestFocus();
                    Toast.makeText(Login.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    return;
                }
                loginUser();
            }

            private void loginUser() {

                final String username=user_id.getText().toString().trim();
                final String password=userpwd.getText().toString().trim();
                //Toast.makeText(Login.this, username+"-"+password, Toast.LENGTH_SHORT).show();
                String loginurl="https://dreambsys.in/codeigniter/hriday/api/authentication/login";

                pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading");
                pDialog.setCancelable(false);
                pDialog.show();

                StringRequest stringRequest=new StringRequest(Request.Method.POST, loginurl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
                        parseData(response);
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error",error.toString()+"");
                        pDialog.hide();
                    }
                }){
                    protected Map<String, String> getParams(){
                        Map<String,String> params=new HashMap<String,String>();
                        params.put("email",username);
                        params.put("password",password);
                        return params;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(Login.this);
                requestQueue.add(stringRequest);
            }

            private void parseData(String response) {
                try {
                    //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.d("Response", "" + response.toString());
                    if (jsonObject.getString("status").equals("true")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        datauser_id = data.getString("user_id");
                        emailid = data.getString("email");
                        fname = data.getString("first_name");

                        // Stored values in session that is shared preference
                        SharedPreferences.Editor editor=getSharedPreferences("user_details",MODE_PRIVATE).edit();
                        editor.putString("user_id",datauser_id);
                        editor.putString("alldetails",response.toString());
                        editor.putBoolean("is_login",true);
                        editor.apply();

                        Intent intent = new Intent(Login.this, Person2person_view.class);
                        startActivity(intent);
                        Login.this.finish();

                        //Toast.makeText(Login.this, "Loggedin successfully.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}

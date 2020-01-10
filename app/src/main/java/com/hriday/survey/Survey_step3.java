package com.hriday.survey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Survey_step3 extends AppCompatActivity {

    Button next3_btn,galpic3_upload;
    RelativeLayout step3_relative;
    RadioButton place_yes, place_no;
    RadioGroup rdb3_option;
    List<String> imagesEncodedList;
    String imageEncoded;
    private GalleryAdapterStep3 galleryAdapterstep3;
    private GridView gvGallery3;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String encodedImage;
    JSONObject jsonObject;
    JSONObject Response;
    ArrayList<String> image_data = new ArrayList<String>();
    int PICK_IMAGE_MULTIPLE=1;
    EditText exact_loc_step3,lat_step3,long_step3,remark_step3;

    Random random = new Random();
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STR_LENGTH = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_step3);

       /* Bundle bundle = getIntent().getExtras();
        final String surveydata_get = bundle.getString("survey_data");
        final String survey_step1_get = bundle.getString("survey_step1");
        final String survey_step2_get = bundle.getString("survey_step2");*/

        rdb3_option=findViewById(R.id.step3_radio_gp);
        step3_relative=findViewById(R.id.step3_upload_section);

        gvGallery3=findViewById(R.id.gvstep3);

        galpic3_upload=findViewById(R.id.picstep3_upload);
        galpic3_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1002);
            }
        });


        rdb3_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);

                if(rb.getText().toString().equalsIgnoreCase("Yes")){
                    //Toast.makeText(Survey_step1.this, "Yes", Toast.LENGTH_SHORT).show();
                    step3_relative.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                    step3_relative.setVisibility(View.GONE);
                }
            }
        });

        next3_btn=findViewById(R.id.next3step_btn);
        next3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(Survey_step3.this, SweetAlertDialog.WARNING_TYPE)
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
                                String survey_update="https://dreambsys.in/codeigniter/hriday/api/surveyor/updatesstep3_survey";
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, survey_update, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Toast.makeText(Survey_step2.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        surveyform_step3(response);
                                        //pDialog.hide();
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Survey_step3.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("Error",error.toString()+"");
                                    }
                                }){
                                    protected Map<String, String> getParams(){
                                        Map<String,String> params=new HashMap<String,String>();

                                        exact_loc_step3=findViewById(R.id.exact_location_step3);
                                        String exactloc_step3=exact_loc_step3.getText().toString();

                                        lat_step3=findViewById(R.id.lat_step3);
                                        String latitudestep3= lat_step3.getText().toString();

                                        long_step3=findViewById(R.id.long_step3);
                                        String longitudestep3= long_step3.getText().toString();

                                        remark_step3=findViewById(R.id.remark_step3);
                                        String remarkstep3= remark_step3.getText().toString();

                                        SharedPreferences survey_formid=getSharedPreferences("surveyform", MODE_PRIVATE);
                                        String survey_id = survey_formid.getString("surveyform_id","");

                                        if(rdb3_option.getCheckedRadioButtonId() == findViewById(R.id.step3_yes_place).getId()) {

                                            params.put("is_site", "1");
                                        }else{
                                            params.put("is_site", "0");
                                        }

                                        params.put("survey_id",survey_id);
                                        params.put("exact_loc_step3",exactloc_step3);
                                        params.put("lat_step3",latitudestep3);
                                        params.put("long_step3",longitudestep3);
                                        params.put("remarksstep3",remarkstep3);

                                            /*} catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue= Volley.newRequestQueue(Survey_step3.this);
                                requestQueue.add(stringRequest);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });
    }

    private void surveyform_step3(String response) {
        // Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show();
        try {
            //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(response);
            Log.d("testquery",""+jsonObject);
            if (jsonObject.getString("status").equals("true")) {

                Intent intent = new Intent(Survey_step3.this, Survey_step4.class);
                startActivity(intent);

                Toast.makeText(Survey_step3.this, "Data has been updated successfully.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Survey_step3.this, "There is an error. Please contact to admin.", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(SurveyForm.this, "TEST", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1002:
                try {
                    // When an Image is picked
                    if (requestCode == 1002 && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data

                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        imagesEncodedList = new ArrayList<String>();
                        if(data.getData()!=null){

                            Uri mImageUri=data.getData();

                            // Get the cursor
                            Cursor cursor = getContentResolver().query(mImageUri,
                                    filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            cursor.close();

                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            mArrayUri.add(mImageUri);
                            galleryAdapterstep3 = new GalleryAdapterStep3(getApplicationContext(),mArrayUri);
                            gvGallery3.setAdapter(galleryAdapterstep3);
                            gvGallery3.setVerticalSpacing(gvGallery3.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery3
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery3.getHorizontalSpacing(), 0, 0);

                        } else {
                            if (data.getClipData() != null) {
                                ClipData mClipData = data.getClipData();


                                for (int i = 0; i < mClipData.getItemCount(); i++) {

                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();

                                    mArrayUri.add(uri);

                                    // for custom code
                                    InputStream imageStream = getContentResolver().openInputStream(uri);
                                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                    encodedImage = encodeImage(selectedImage);
                                    //end here

                                    // Get the cursor
                                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                    // Move to first row
                                    cursor.moveToFirst();

                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    imageEncoded  = cursor.getString(columnIndex);
                                    imagesEncodedList.add(imageEncoded);
                                    cursor.close();

                                    galleryAdapterstep3 = new GalleryAdapterStep3(getApplicationContext(),mArrayUri);
                                    gvGallery3.setAdapter(galleryAdapterstep3);
                                    gvGallery3.setVerticalSpacing(gvGallery3.getHorizontalSpacing());
                                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery3
                                            .getLayoutParams();
                                    mlp.setMargins(0, gvGallery3.getHorizontalSpacing(), 0, 0);

                                }
                                new UploadImages_step3().execute();
                                //new Survey_step1.UploadImages().execute();
                                Log.d("LOG_TAG", "Selected Images" + mArrayUri.size());
                                //Log.d("Imageschecking", "Images" + image_data.toString());
                            }
                        }
                    } else {
                        Toast.makeText(this, "You haven't picked Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    // Convert to encode base 64
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    //Check all required permission
    public void checkpermission(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(Survey_step3.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
        }
    }

    public String getRandomString(){
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < RANDOM_STR_LENGTH; i++) {
            int number = getRandomNumber();
            char ch = _CHAR.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
    private int getRandomNumber() {
        int randomInt = 0;
        randomInt = random.nextInt(_CHAR.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    private class UploadImages_step3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                // Log.d("Vicky", "encodedImage = " + encodedImage);
                //Log.d("Vicky", "encodedImage = " + imageEncoded);

                for (int i = 0; i < mArrayUri.size(); i++) {
                    //Log.d("Image_encode", String.valueOf(mArrayUri.get(i)));
                    //Log.d("Vicky", "encodedImage = " + mArrayUri.get(i));
                    jsonObject = new JSONObject();
                    InputStream imageStream = getContentResolver().openInputStream(mArrayUri.get(i));
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    encodedImage = encodeImage(selectedImage);
                    //Log.d("Image_encode", encodedImage);

                    // Check if already login or not
                    SharedPreferences survey_formid=getSharedPreferences("surveyform", MODE_PRIVATE);
                    String survey_id = survey_formid.getString("surveyform_id","");
                    //Toast.makeText(this, ""+islogin, Toast.LENGTH_SHORT).show();


                    jsonObject.put("imageString", encodedImage);
                    jsonObject.put("imageName", getRandomString());
                    jsonObject.put("survey_id", survey_id);
                    jsonObject.put("source_type","3");
                    jsonObject.put("media_type","4");
                    String data = jsonObject.toString();
                    String uploadimg = "https://dreambsys.in/codeigniter/hriday/api/UploadSurveyor/uploadimage/";
                    URL url = new URL(uploadimg);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(data.getBytes().length);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(data);
                    //Log.d("Vicky", "Data to php = " + data);
                    writer.flush();
                    writer.close();
                    out.close();
                    connection.connect();

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            in, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();
                    String result = sb.toString();
                    connection.disconnect();
                }


            } catch (Exception e) {
                Log.d("Vicky", "Error Encountered");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

        }
    }
}

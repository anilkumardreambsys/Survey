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

public class Survey_step2 extends AppCompatActivity {

    Button step2_next,galpic_upload;
    RadioGroup rdb_optionstep2;
    RadioButton place_yes, place_no;
    RelativeLayout step2_upload;
    List<String> imagesEncodedList;
    String imageEncoded;
    private GalleryAdapterStep2 galleryAdapterstep2;
    private GridView gvGallery2;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String encodedImage;
    JSONObject jsonObject;
    JSONObject Response;
    ArrayList<String> image_data = new ArrayList<String>();
    int PICK_IMAGE_MULTIPLE=1;
    EditText exact_loc_step2,lat_step2,long_step2,remark_step2;

    Random random = new Random();
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STR_LENGTH = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_step2);

        //Bundle bundle = getIntent().getExtras();
        //final String surveydata_get = bundle.getString("survey_data");
        //final String survey_step1_get = bundle.getString("survey_step1");
        //Log.d("Response","surveydata_get="+surveydata_get);
        //Log.d("Response1","survey_step1="+survey_step1);

        step2_next=findViewById(R.id.next2step_btn);
        place_yes=findViewById(R.id.step2_yes_place);
        place_no=findViewById(R.id.step2_no_place);
        rdb_optionstep2=findViewById(R.id.step2_options);
        step2_upload=findViewById(R.id.step2_upload_section);

        galpic_upload=findViewById(R.id.galpic_upload);
        galpic_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1002);
            }
        });

        gvGallery2 =findViewById(R.id.gvstep2);
        checkpermission();
        rdb_optionstep2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);

                if(rb.getText().toString().equalsIgnoreCase("Yes")){
                    //Toast.makeText(Survey_step1.this, "Yes", Toast.LENGTH_SHORT).show();
                    step2_upload.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                    step2_upload.setVisibility(View.GONE);
                }
                //Toast.makeText(Survey_step1.this, "-"+rb.getText()+"-", Toast.LENGTH_SHORT).show();
            }
        });


        step2_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final RadioButton rb_noplace=(RadioButton)findViewById(R.id.step2_no_place);   // No selected Status
                final RadioButton rb_yesplace=(RadioButton)findViewById(R.id.step2_yes_place); // Yes selected status

                new SweetAlertDialog(Survey_step2.this, SweetAlertDialog.WARNING_TYPE)
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
                                String survey_update="https://dreambsys.in/codeigniter/hriday/api/surveyor/updatesstep2_survey";
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, survey_update, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Toast.makeText(Survey_step2.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        surveyform_step2(response);
                                        //pDialog.hide();
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Survey_step2.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("Error",error.toString()+"");
                                    }
                                }){
                                    protected Map<String, String> getParams(){
                                        Map<String,String> params=new HashMap<String,String>();

                                        exact_loc_step2=findViewById(R.id.exact_location_step2);
                                        String exactloc_step2=exact_loc_step2.getText().toString();

                                        lat_step2=findViewById(R.id.lat_step2);
                                        String latitudestep2= lat_step2.getText().toString();

                                        long_step2=findViewById(R.id.long_step2);
                                        String longitudestep2= long_step2.getText().toString();

                                        remark_step2=findViewById(R.id.remark_step2);
                                        String remarkstep2= remark_step2.getText().toString();

                                        SharedPreferences survey_formid=getSharedPreferences("surveyform", MODE_PRIVATE);
                                        String survey_id = survey_formid.getString("surveyform_id","");

                                        if(rdb_optionstep2.getCheckedRadioButtonId() == findViewById(R.id.step2_yes_place).getId()) {

                                            params.put("isevent_site", "1");
                                        }else{
                                            params.put("isevent_site", "0");
                                        }


                                        /*if (getinfo=="No") {
                                            params.put("isevent_site", "0");
                                        } else {
                                            params.put("isevent_site", "1");
                                        }*/


                                        params.put("survey_id",survey_id);
                                        params.put("exact_loc_step2",exactloc_step2);
                                        params.put("lat_step2",latitudestep2);
                                        params.put("long_step2",longitudestep2);
                                        params.put("remarksstep2",remarkstep2);

                                            /*} catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue= Volley.newRequestQueue(Survey_step2.this);
                                requestQueue.add(stringRequest);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
        });

    }

    private void surveyform_step2(String response) {
        // Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show();
        try {
            //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(response);
            Log.d("testquery",""+jsonObject);
            if (jsonObject.getString("status").equals("true")) {

                Intent intent = new Intent(Survey_step2.this, Survey_step3.class);
                startActivity(intent);

                Toast.makeText(Survey_step2.this, "Data has been updated successfully.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Survey_step2.this, "There is an error. Please contact to admin.", Toast.LENGTH_SHORT).show();
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
                            galleryAdapterstep2 = new GalleryAdapterStep2(getApplicationContext(),mArrayUri);
                            gvGallery2.setAdapter(galleryAdapterstep2);
                            gvGallery2.setVerticalSpacing(gvGallery2.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery2
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery2.getHorizontalSpacing(), 0, 0);

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

                                    galleryAdapterstep2 = new GalleryAdapterStep2(getApplicationContext(),mArrayUri);
                                    gvGallery2.setAdapter(galleryAdapterstep2);
                                    gvGallery2.setVerticalSpacing(gvGallery2.getHorizontalSpacing());
                                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery2
                                            .getLayoutParams();
                                    mlp.setMargins(0, gvGallery2.getHorizontalSpacing(), 0, 0);

                                }
                                new UploadImages_step2().execute();
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
            ActivityCompat.requestPermissions(Survey_step2.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
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

    private class UploadImages_step2 extends AsyncTask<Void, Void, Void> {

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
                    jsonObject.put("source_type","2");
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

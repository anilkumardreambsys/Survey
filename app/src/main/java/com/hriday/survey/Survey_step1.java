package com.hriday.survey;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Survey_step1 extends AppCompatActivity {

    Button next1_btn , record_audio,record_video,upload_imgcamera,upload_img_gall,playaudio;
    RadioGroup rdb_option;
    RadioButton place_yes, place_no;
    RelativeLayout step1_upload;
    ImageView iv_camera_img,iv_gal_img;
    int PICK_IMAGE_MULTIPLE=1;
    String imageEncoded;
    String encodedImage;
    JSONObject jsonObject;
    JSONObject Response;
    List<String> imagesEncodedList;
    private GridView gvGallery,gvCamera;
    private CameraAdapterStep1 camera_step1;
    private GalleryAdapterStep1 galleryAdapter;
    ArrayList<String> image_data = new ArrayList<String>();
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    EditText exact_loc_step1,lat_step1,long_step1,remark_step1;

    String AudioSavePathInDevice = null;

    Random random = new Random();
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STR_LENGTH = 12;
    FusedLocationProviderClient mFusedLocationClient;

    /*========= Audio recording ======*/
    private String mFileName  = null;
    private MediaRecorder mRecorder  = null;
    private MediaPlayer mediaPlayer  = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    /*========= Audio recording end======*/

    private Uri mImageUri;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_step1);
        record_audio=findViewById(R.id.record_audio_step1);
        playaudio=findViewById(R.id.playbtn);
        playaudio.setVisibility(View.GONE);
        //Audio playing
        playaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG,"Onclick");
                mediaPlayer=new MediaPlayer();
                String media_path=AudioSavePathInDevice;
                Log.d("Fileaudio",""+media_path);
                //String media_path= "/storage/emulated/0/IXGASCWQC7A6AudioRecording.mp3";
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Uri uri= Uri.parse(media_path);
               // Log.d(TAG,"Player initiated");
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playback started", Toast.LENGTH_LONG).show();
                    playaudio.setEnabled(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        playaudio.setEnabled(true);
                        mediaPlayer.release();
                        mediaPlayer = null;
                        Toast.makeText(getApplicationContext(), "Playback finished", Toast.LENGTH_LONG).show();
                    }
                });
            }

        });


        // Audio recording
        record_audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //if Button is Pressed.! or user Id Holding Button
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    Toast.makeText(Survey_step1.this, "Hold To Record.!", Toast.LENGTH_SHORT).show();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Do Nothing
                    stopRecording();
                    Toast.makeText(Survey_step1.this, "Recorded.!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


        next1_btn=findViewById(R.id.nextstep1_btn);
        place_yes=findViewById(R.id.yes_place);
        place_no=findViewById(R.id.no_place);
        rdb_option=findViewById(R.id.rd_place_site);
        step1_upload=findViewById(R.id.upload_section);
        upload_imgcamera= findViewById(R.id.upload_camimg_step1);
        //step1_upload.setVisibility(View.GONE);
        iv_camera_img=findViewById(R.id.iv_camgal);

        gvGallery =findViewById(R.id.gv);

        checkpermission();
        exact_loc_step1=findViewById(R.id.exact_location_step1);
        lat_step1=findViewById(R.id.lat_step1);
        long_step1=findViewById(R.id.long_step1);
        // Camera open
        upload_imgcamera=findViewById(R.id.upload_camimg_step1);

        upload_imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // takeAPicture();
                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //startActivityForResult(cameraIntent, 1001);
               // Toast.makeText(Survey_step1.this, "Test", Toast.LENGTH_SHORT).show();
                openCameraIntent();

            }
        });


        //Gallery open
        upload_img_gall= findViewById(R.id.upload_galimg_step1);
        upload_img_gall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1002);
            }
        });

        rdb_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);

                if(rb.getText().toString().equalsIgnoreCase("Yes")){
                    //Toast.makeText(Survey_step1.this, "Yes", Toast.LENGTH_SHORT).show();
                    step1_upload.setVisibility(View.VISIBLE);
                }else{
                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                    step1_upload.setVisibility(View.GONE);
                }
                //Toast.makeText(Survey_step1.this, "-"+rb.getText()+"-", Toast.LENGTH_SHORT).show();
            }
        });
        next1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewid=rdb_option.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(viewid);
                final String getinfo=radioButton.getText().toString();


                JSONObject assignjson_obj = new JSONObject();
                JSONArray jsonarray_data = new JSONArray();
                Bundle bundle = new Bundle();
                RadioButton rb_noplace=(RadioButton)findViewById(R.id.no_place);   // No selected Status
                RadioButton rb_yesplace=(RadioButton)findViewById(R.id.yes_place); // Yes selected status

                try {
                    if (rb_noplace.getText().toString().equalsIgnoreCase("No")) {
                        assignjson_obj.put("isplace_site", "0");
                    } else {
                        assignjson_obj.put("isplace_site", "1");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                // Post submission checking start here....
                new SweetAlertDialog(Survey_step1.this, SweetAlertDialog.WARNING_TYPE)
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
                                String survey_update="https://dreambsys.in/codeigniter/hriday/api/surveyor/updatesstep1_survey";
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, survey_update, new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Toast.makeText(Survey_step1.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        surveyform_step1(response);
                                        //pDialog.hide();
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Survey_step1.this, error.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("Error",error.toString()+"");
                                    }
                                }){
                                    protected Map<String, String> getParams(){
                                        Map<String,String> params=new HashMap<String,String>();

                                        exact_loc_step1=findViewById(R.id.exact_location_step1);
                                        String exactloc_step1=exact_loc_step1.getText().toString();

                                        lat_step1=findViewById(R.id.lat_step1);
                                        String latitudestep1= lat_step1.getText().toString();

                                        long_step1=findViewById(R.id.long_step1);
                                        String longitudestep1= long_step1.getText().toString();

                                        remark_step1=findViewById(R.id.remark_step1);
                                        String remarkstep1= remark_step1.getText().toString();

                                        SharedPreferences survey_formid=getSharedPreferences("surveyform", MODE_PRIVATE);
                                        String survey_id = survey_formid.getString("surveyform_id","");

                                        if (getinfo=="No") {
                                            params.put("isplace_site", "0");
                                        } else {
                                            params.put("isplace_site", "1");
                                        }

                                        params.put("survey_id",survey_id);
                                        params.put("exact_loc_step1",exactloc_step1);
                                        params.put("lat_step1",latitudestep1);
                                        params.put("long_step1",longitudestep1);
                                        params.put("remarksstep1",remarkstep1);

                                        return params;
                                    }
                                };
                                RequestQueue requestQueue= Volley.newRequestQueue(Survey_step1.this);
                                requestQueue.add(stringRequest);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


            }
        });
    }

    private void startRecording() {



        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        getRandomString()+ "AudioRecording.mp3";

        MediaRecorderReady();

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("FAiledrec", "prepare() failed");
        }

        mRecorder.start();

        Log.e("CALKLIG", "Recording:Calling ");

    }

    public void MediaRecorderReady(){
        mRecorder=new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(AudioSavePathInDevice);

    }

    private void stopRecording() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("savevideo",AudioSavePathInDevice);
        Log.d("SAvedAudio",""+AudioSavePathInDevice);
        setResult(RESULT_OK,returnIntent);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        new FtpUpload().execute();
        playaudio.setVisibility(View.VISIBLE);
        Log.e("tesing123", "stopRecording:Calling ");

    }

    public void getLastLocation(){
        if (checkpermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat_step1.setText(location.getLatitude()+"");
                                    long_step1.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat_step1.setText(mLastLocation.getLatitude()+"");
            long_step1.setText(mLastLocation.getLongitude()+"");
        }
    };

    private void surveyform_step1(String response) {
        try {
            //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status").equals("true")) {

                Intent intent = new Intent(Survey_step1.this, Survey_step2.class);
                startActivity(intent);

                Toast.makeText(Survey_step1.this, "Data has been updated successfully.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Survey_step1.this, "There is an error. Please contact to admin.", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(SurveyForm.this, "TEST", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int PIC_CODE=0;


    String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private File output=null;
    private void openCameraIntent() {

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File dir=
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        output=new File(dir, "CameraContentDemo.jpeg");
        //pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        //setA
        startActivityForResult(pictureIntent, 1001);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                //Log.d("Testing123","sdffdsdfsdf");
                //Toast.makeText(this, "Getback", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, ""+requestCode+"-"+RESULT_OK, Toast.LENGTH_SHORT).show();
                   if (requestCode == 1001 && resultCode == RESULT_OK) {
                       //Log.d("Testing123","sdffdsdfsdf"+imageFilePath);
                       Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                       ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                       thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                       File destination = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
                       FileOutputStream fo;
                       try {
                           fo = new FileOutputStream(destination);
                           fo.write(bytes.toByteArray());
                           fo.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       new uploadFileToServerTask().execute(destination.getAbsolutePath());
                   }

                break;
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
                            galleryAdapter = new GalleryAdapterStep1(getApplicationContext(),mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

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
                                    //image_data.add(encodedImage);

                                    // image_data.add("test1="+encodedImage);
                                    //end here

                                    // Get the cursor
                                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                    // Move to first row
                                    cursor.moveToFirst();

                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    imageEncoded  = cursor.getString(columnIndex);
                                    imagesEncodedList.add(imageEncoded);
                                    cursor.close();

                                    galleryAdapter = new GalleryAdapterStep1(getApplicationContext(),mArrayUri);
                                    gvGallery.setAdapter(galleryAdapter);
                                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                            .getLayoutParams();
                                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                                }
                                new UploadImages().execute();
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


    private class uploadFileToServerTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... args) {
            try {
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                @SuppressWarnings("PointlessArithmeticExpression")
                int maxBufferSize = 1 * 1024 * 1024;

                String uploadimg = "https://dreambsys.in/codeigniter/hriday/api/UploadSurveyor/cameraupload/";
                java.net.URL url = new URL(uploadimg);
                Log.d("URL_PATH", "url " + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Allow Inputs &amp; Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                FileInputStream fileInputStream;
                DataOutputStream outputStream;
                {
                    outputStream = new DataOutputStream(connection.getOutputStream());

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    String filename = args[0];
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    Log.d("Filename", "filename " + filename);

                    fileInputStream = new FileInputStream(filename);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];

                    // Read file
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.d("serverResponseCode", "" + serverResponseCode);
                Log.d("serverResponseMessage", "" + serverResponseMessage);

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                if (serverResponseCode == 200) {
                    return "true";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(Object result) {

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


    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1000
        );
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                //checkpermission();
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
               // checkpermission();
            }
            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                //checkpermission();
            }
            if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                //checkpermission();
            }
        }

    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
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


    public class FtpUpload extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

            FTPClient con = null;

            try {
                con = new FTPClient();  // con is the FTPClient
                con.connect("103.16.146.195"); //set the ip address of the server
                Log.d("upload_result", "succeeded");
                if (con.login("dsysin607", "Dream@xyz")) {   // checks if login is successful
                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    //String data = "/storage/emulated/0/N73ULC1E6W2VAudioRecording.3gp";


                    Log.d("videolink",""+AudioSavePathInDevice);
                    //String data = getIntent().getExtras().getString("savevideo");
                    FileInputStream in = new FileInputStream(new File(AudioSavePathInDevice));
                    boolean result = con.storeFile("/public_html/codeigniter/hriday/assets/encrypt_surveyform/audio/place/record.mp3", in);  // where to store in the server
                    if (result) {
                        Log.v("upload result", "succeeded");

//Toast.makeText(getApplicationContext(), "upload Sucess",Toast.LENGTH_SHORT).show();
                    }
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error_uploading",e.getMessage());
            }


            return null;
        }
    }




        private class UploadImages extends AsyncTask<Void, Void, Void> {

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
                    jsonObject.put("source_type","1");
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

                /*jsonObject = new JSONObject();
                jsonObject.put("imageString", encodedImage);
                jsonObject.put("imageName", getRandomString());
                String data = jsonObject.toString();
                String uploadimg = "http://dreambsys.in/codeigniter/hriday/api/UploadSurveyor/uploadimage/";
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
                Log.d("Vicky", "Response from php = " + result);
                //Response = new JSONObject(result);
                connection.disconnect();*/

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

package com.hriday.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Person2person_view extends AppCompatActivity {

    Button addmore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person2person_view);

        addmore=findViewById(R.id.addmore_loc);
        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Person2person_view.this, SurveyForm.class);
                startActivity(intent);
            }
        });
    }
}

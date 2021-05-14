package com.example.duckdating.ui.match;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duckdating.R;
import com.parse.ParseObject;

public class MatchProfileActivity extends AppCompatActivity {
    static final String TAG = "MatchProfileActivity";

    String name, location, gender, bio, matchEmail;
    String email;
    byte[] imageBytes;

    private TextView nameText, locationText, genderText, bioText;
    private ImageView pic;
    private Button message;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_profile);

        //transfer from intent
        name = getIntent().getStringExtra("name");
        location = getIntent().getStringExtra("location");
        gender = getIntent().getStringExtra("gender");
        bio = getIntent().getStringExtra("bio");
        email = getIntent().getStringExtra("email");
        matchEmail = getIntent().getStringExtra("matchEmail");
        imageBytes = getIntent().getByteArrayExtra("imageBytes");

        //set up views
        nameText = findViewById(R.id.name);
        locationText = findViewById(R.id.location);
        genderText = findViewById(R.id.gender);
        bioText = findViewById(R.id.bio);
        pic = findViewById(R.id.pic);
        message = findViewById(R.id.messageBtn);

        //fill up views
        nameText.setText(name);
        locationText.setText(location);
        genderText.setText(gender);
        bioText.setText(bio);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        pic.setImageBitmap(bitmap);

        //set up message button
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchProfileActivity.this, ChatActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("matchEmail", matchEmail);
                startActivity(intent);
            }
        });


    }
}

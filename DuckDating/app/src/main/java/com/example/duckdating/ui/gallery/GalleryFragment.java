package com.example.duckdating.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.duckdating.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    private GalleryViewModel galleryViewModel;

    private TextView name, location, gender, bio, email, otherEmail;
    private ImageView pic;
    private Button goLeft, match, goRight;

    private int position;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        galleryViewModel = new ViewModelProvider(this, new GalleryViewModelFactory(getActivity().getApplication())).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        //grab views
        name = root.findViewById(R.id.name);
        location = root.findViewById(R.id.location);
        gender = root.findViewById(R.id.gender);
        bio = root.findViewById(R.id.bio);
        pic = root.findViewById(R.id.pic);
        email = root.findViewById(R.id.email); //stays invisible, only for retreival purposes
        otherEmail = root.findViewById(R.id.otherEmail); //same as above

        goLeft = root.findViewById(R.id.goLeft);
        match = root.findViewById(R.id.match);
        goRight = root.findViewById(R.id.goRight);

        //grabs first object
        position = 0;
        grabObject(position);

        //stores email here
        galleryViewModel.getEmail().observe(getViewLifecycleOwner(), s -> {email.setText(s);});

        goLeft.setEnabled(false);
        goLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                grabObject(position);

                if(position == 0){
                    goLeft.setEnabled(false);
                }
            }
        });

        goRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                grabObject(position);

                if(position != 0){
                    goLeft.setEnabled(true);
                }
            }
        });

        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject object = new ParseObject("Match");
                Log.v(TAG, "email: " + email.getText().toString());
                Log.v(TAG, "otherEmail: " + otherEmail.getText().toString());
                object.put("email", email.getText().toString());
                object.put("otherEmail", otherEmail.getText().toString());
                object.saveInBackground(e -> {
                    if (e == null){
                        Log.v("GalleryView", "One sided match made!");
                    }
                    else{
                        Log.v("GalleryView", "error");
                    }
                });
                match.setEnabled(false);
                match.setText("Already Matched");
            }
        });

        return root;
    }

    private void grabObject(int pos){
        //grabs object from position pos and displays it
        galleryViewModel.getListDucks().observe(getViewLifecycleOwner(), listDucks -> {
            //ensures position is never beyond size of list
            if (pos == listDucks.size() - 1){
                goRight.setEnabled(false);
            }
            else{
                goRight.setEnabled(true);
            }

            //grabs object and sets up view
            ParseObject example = listDucks.get(pos);
            setupView(example);
        });

    }

    private void setupView(ParseObject object){
        name.setText(object.get("firstName") + " " + object.get("lastName"));
        location.setText(object.get("location").toString());
        gender.setText(object.get("gender").toString());
        bio.setText(object.get("bio").toString());
        otherEmail.setText(object.get("email").toString());

        //sets up image
        ParseFile imageFile = (ParseFile) object.get("pic");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                pic.setImageBitmap(bmp);
            }
        });

        //ensures that user has not already matched this person
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        query.whereEqualTo("email", email.getText().toString());
        query.whereEqualTo("otherEmail", object.get("email").toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0){
                    match.setEnabled(false);
                    match.setText("Already Matched");
                }
                else{
                    match.setEnabled(true);
                    match.setText("Match");
                }
            }
        });
    }
}
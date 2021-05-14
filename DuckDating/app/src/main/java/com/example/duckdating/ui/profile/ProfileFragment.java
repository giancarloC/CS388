package com.example.duckdating.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.duckdating.R;
import com.example.duckdating.ui.login.LoginViewModel;
import com.example.duckdating.ui.login.LoginViewModelFactory;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static String TAG = "ProfileFragment";

    private int GALLERY_REQUEST = 420;

    EditText email, firstName, lastName, location, bio, password;
    TextView gender, image;
    Button saveBtn;

    private ProfileViewModel profileViewModel;
    private Uri selectedImage;
    private Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(getActivity().getApplication()))
                        .get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        //grab views
        email = root.findViewById(R.id.email);
        firstName = root.findViewById(R.id.firstName);
        lastName = root.findViewById(R.id.lastName);
        location = root.findViewById(R.id.location);
        bio = root.findViewById(R.id.bio);
        password = root.findViewById(R.id.password);
        gender = root.findViewById(R.id.gender);
        image = root.findViewById(R.id.image);
        saveBtn = root.findViewById(R.id.saveBtn);

        final ProgressBar loadingProgressBar = root.findViewById(R.id.loading);

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), s -> {
            //populate views with already known information
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
            query.whereEqualTo("email", s);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {
                        ParseObject o = objects.get(0);
                        lastName.setText(o.getString("lastName"));
                        location.setText(o.getString("location"));
                        bio.setText(o.getString("bio"));
                        gender.setText(o.getString("gender"));
                    } else {
                        // Something went wrong.
                    }
                }
            });
            email.setText(s);
        });
        profileViewModel.getDisplayName().observe(getViewLifecycleOwner(), s -> firstName.setText(s));




        //add listeners to respective places
        //thing for choosing a gender
        String[] genders = {"Male", "Female", "Nonbinary", "Other"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your Gender");
        builder.setItems(genders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(genders[which]);
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        //choosing an image
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        //code up the update process


        profileViewModel.getEmail().observe(getViewLifecycleOwner(), s -> email.setText(s));

        saveBtn.setOnClickListener((view)->{
            profileViewModel.saveChanges(email.getText().toString(), firstName.getText().toString(),
                    lastName.getText().toString(), location.getText().toString(), bio.getText().toString(),
                    password.getText().toString(), gender.getText().toString(), bitmap);
        });
        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                selectedImage = data.getData();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), selectedImage));
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    }
                } catch (IOException e) {
                    Log.i(TAG, "Some exception: " + e);
                }

                //change text to file path
                String[] path = selectedImage.getPath().split("/");
                Log.v(TAG, "path: " + selectedImage.getPath());
                image.setText(path[path.length - 1] + "." + path[path.length - 2]);
            }
        }
    }


}
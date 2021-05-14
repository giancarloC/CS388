package com.example.duckdating.ui.home;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.duckdating.R;
import com.example.duckdating.data.LoginDataSource;
import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.model.LoggedInUser;
import com.example.duckdating.ui.profile.ProfileViewModel;
import com.example.duckdating.ui.profile.ProfileViewModelFactory;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private LoginRepository loginRepository;
    private Context context;

    private RelativeLayout loggedOut, loggedIn;
    private TextView name, location, gender, bio, email;
    private ImageView pic;
    LifecycleOwner owner;
    LoggedInUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory(getActivity().getApplication())).get(HomeViewModel.class);
        owner = getViewLifecycleOwner();

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        //grabs views
        loggedIn = root.findViewById(R.id.loggedIn);
        loggedOut = root.findViewById(R.id.loggedOut);
        name = root.findViewById(R.id.name);
        location = root.findViewById(R.id.location);
        gender = root.findViewById(R.id.gender);
        bio = root.findViewById(R.id.bio);
        pic = root.findViewById(R.id.pic);
        email = root.findViewById(R.id.email); //used only for reference to email

        //homeViewModel.getDisplayName().observe(getViewLifecycleOwner(), s -> name.setText(s));

        //checks if user has logged in
        loginRepository = LoginRepository.getInstance(new LoginDataSource(), getApplicationContext());
        loginRepository.isLoggedIn().observe(getActivity(),(isLoggedIn)->{
            Log.v("isLoggedIn", isLoggedIn?"true":"false");
            if(isLoggedIn) {
                loggedOut.setVisibility(View.INVISIBLE);
                loggedIn.setVisibility(View.VISIBLE);

                homeViewModel.populate();
                homeViewModel.getDisplayName().observe(owner, s -> {name.setText(s);});
                homeViewModel.getEmail().observe(owner, s -> {
                    email.setText(s);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
                    query.whereEqualTo("email", email.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null){
                                ParseObject object = objects.get(0); //should be only 1 object in list
                                location.setText(object.get("location").toString());
                                gender.setText(object.get("gender").toString());
                                bio.setText(object.get("bio").toString());
                                String firstName = name.getText().toString();
                                name.setText("You: " + firstName + " " + object.get("lastName").toString());

                                //sets up image
                                ParseFile imageFile = (ParseFile) object.get("pic");
                                imageFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        pic.setImageBitmap(bmp);
                                    }
                                });
                            }
                        }
                    });
                });

            }

            //user has not logged in yet
            else{
                loggedOut.setVisibility(View.VISIBLE);
                loggedIn.setVisibility(View.INVISIBLE);
            }
        });



        return root;
    }
}
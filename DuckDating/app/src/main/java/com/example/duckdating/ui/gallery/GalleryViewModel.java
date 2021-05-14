package com.example.duckdating.ui.gallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.model.LoggedInUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    LoginRepository loginRepository;
    LoggedInUser user;

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<List<ParseObject>> listDucks = new MutableLiveData<>();

    public GalleryViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        user = this.loginRepository.getUser();
        String userEmail = user.getEmail();
        email.setValue(userEmail);

        //grabs list of other ducks
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
        query.whereNotEqualTo("email", userEmail);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    //make listeners
                    listDucks.setValue(objects);
                }
            }
        });
    }


    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<List<ParseObject>> getListDucks() {
        return listDucks;
    }
}
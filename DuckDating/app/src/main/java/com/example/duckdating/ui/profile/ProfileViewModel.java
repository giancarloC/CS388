package com.example.duckdating.ui.profile;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.Result;
import com.example.duckdating.data.model.LoggedInUser;

public class ProfileViewModel extends ViewModel {
    LoginRepository loginRepository;

    private MutableLiveData<String> displayName = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public ProfileViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        LoggedInUser user = this.loginRepository.getUser();
        displayName.setValue(user.getDisplayName());
        email.setValue((user.getEmail()));
    }

    public LiveData<String> getDisplayName(){return displayName;}


    public LiveData<String> getEmail(){return email;}

    public void saveChanges(String email, String firstName, String lastName, String location,
                            String bio, String password, String gender, Bitmap bitmap){
        loginRepository.updateProfile(email, firstName, lastName, location, bio, password, gender, bitmap, (user)->{
            Log.v("ProfileViewModel", "saved profile");
            LoggedInUser data = ((Result.Success<LoggedInUser>) user).getData();
            this.displayName.setValue(data.getDisplayName());
            this.email.setValue(data.getEmail());
        },(error)->{
            Log.e("ProfileViewModel", "error saving profile");
        });
    }
}


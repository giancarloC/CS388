package com.example.duckdating.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.model.LoggedInUser;

public class HomeViewModel extends ViewModel {
    LoginRepository loginRepository;
    LoggedInUser user;

    private MutableLiveData<String> username = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public HomeViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void populate(){
        user = this.loginRepository.getUser();
        username.setValue(user.getDisplayName());
        email.setValue(user.getEmail());
    }

    public LiveData<String> getDisplayName(){
        return username;
    }

    public LiveData<String> getEmail() {
        return email;
    }

}
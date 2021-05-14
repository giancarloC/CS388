package com.example.duckdating.ui.login;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.Result;
import com.example.duckdating.data.model.LoggedInUser;
import com.example.duckdating.R;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    RegisterViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password) {
        // can be launched in a separate asynchronous job

        loginRepository.login(username, password, (Result<LoggedInUser> success)->{
            //success callback
            Log.v(this.getClass().getSimpleName(), "login success");
            LoggedInUser data = ((Result.Success<LoggedInUser>) success).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        }, (Result.Error error)->{
            //error callback
            Log.v(this.getClass().getSimpleName(), error.getError().getMessage());
            loginResult.setValue(new LoginResult(R.string.login_failed));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String username, String lastName, String location, String bio, Bitmap pic, String gender, String password) {
        // can be launched in a separate asynchronous job

        loginRepository.register(email, username, lastName, location, bio, pic, gender, password, (Result<LoggedInUser> success)->{
            //success callback
            Log.v(this.getClass().getSimpleName(), "register success");
            LoggedInUser data = ((Result.Success<LoggedInUser>) success).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        }, (Result.Error error)->{
            //error callback
            Log.v(this.getClass().getSimpleName(), error.getError().getMessage());
            loginResult.setValue(new LoginResult(R.string.login_failed));
        });
    }

    public void loginDataChanged(String username, String firstName, String lastName, String location, String bio, String password, String gender, String image) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username,
                    null, null, null, null, null, null, null));
        }
        else if (firstName.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    R.string.empty_first, null, null, null, null, null, null));
        }
        else if (lastName.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    null, R.string.empty_last, null, null, null, null, null));
        }
        else if (location.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    null, null, R.string.empty_location, null, null, null, null));
        }
        else if (bio.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    null, null, null, R.string.empty_bio, null, null, null));
        }
        else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null,
                    null, null, null, null, R.string.invalid_password, null, null));
        }
        else if (gender.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    null, null, null, null, null, R.string.empty_gender, null));
        }
        else if (image.isEmpty()){
            loginFormState.setValue(new LoginFormState(null,
                    null, null, null, null, null, null, R.string.empty_image));
        }
        else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }
}
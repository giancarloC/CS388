package com.example.duckdating.ui.login;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;
import android.util.Log;
import android.util.Patterns;

import com.example.duckdating.data.LoginRepository;
import com.example.duckdating.data.Result;
import com.example.duckdating.data.model.LoggedInUser;
import com.example.duckdating.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
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


    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, null, null, null, null, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, null, null, null, null, R.string.invalid_password, null, null));
        } else {
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
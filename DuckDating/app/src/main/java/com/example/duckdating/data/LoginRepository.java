package com.example.duckdating.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import com.example.duckdating.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;
    SharedPreferences sharedpreferences;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    //NOTE: not necessarily the best way to do this
    private MutableLiveData<Boolean> isLoggedIn;

    SharedPreferences prefs;
    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
        if(isLoggedIn == null){
            isLoggedIn = new MutableLiveData<>();
        }

        isLoggedIn.setValue(user != null);
    }

    public static LoginRepository getInstance(LoginDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
            instance.prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            if(instance.prefs.contains("user")){
                String json = instance.prefs.getString("user","{}");
                try {
                    JSONObject jo = new JSONObject(json);
                    if(jo.has("uid") && jo.getString("uid").length() > 0) {
                        instance.setLoggedInUser(
                                new LoggedInUser(
                                        jo.getString("uid"),
                                        jo.getString("displayName"),
                                        jo.getString("email")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }
    //NOTE: not necessarily the best way to do this
    public LiveData<Boolean> isLoggedIn(){
        if(isLoggedIn == null){
            isLoggedIn = new MutableLiveData<>();
        }
        return isLoggedIn;
    }
    /*public boolean isLoggedIn() {
        return user != null;
    }*/

    public void logout() {
        user = null;
        isLoggedIn.setValue(false);
        dataSource.logout();
        localSaveUser();
    }
    public LoggedInUser getUser(){
        return this.user;
    }
    private void localSaveUser(){
        SharedPreferences.Editor prefsEdit = prefs.edit();

        try {
            if(user != null) {
                JSONObject jo = new JSONObject();
                jo.put("uid", user.getUserId());
                jo.put("email", user.getEmail());
                jo.put("displayName", user.getDisplayName());
                prefsEdit.putString("user", jo.toString());
            }
            else{
                prefsEdit.remove("user");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            prefsEdit.commit();
        }
    }
    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        isLoggedIn.setValue(user != null);
        if(user != null){
            localSaveUser();

        }
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
    public void updateProfile(String email, String firstName, String lastName, String location, String bio,
            String password, String gender, Bitmap bitmap,Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error){
        dataSource.updateProfile(this.user.getUserId(), email, firstName, lastName, location, bio, password,
                gender, bitmap, success, error);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        // handle login
        dataSource.login(username, password,
                (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    error.accept(err);
                });
    }

    public void register(String email, String username, String lastName, String location, String bio, Bitmap pic, String gender, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        dataSource.register(email, username, lastName, location, bio, pic, gender, password, (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    error.accept(err);
                });
    }
}
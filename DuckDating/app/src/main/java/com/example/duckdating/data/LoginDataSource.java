package com.example.duckdating.data;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.example.duckdating.data.model.LoggedInUser;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private LoggedInUser user;
    public void updateProfile(String uid, String email, String firstName, String lastName, String location,
            String bio, String password, String gender, Bitmap bitmap, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error){
        try {
            ANRequest.PostRequestBuilder req = AndroidNetworking.post("https://class.whattheduck.app/api/updateProfile");
            req.addBodyParameter("uid", uid);
            if(firstName != null){
                req.addBodyParameter("username", firstName);
            }
            if(email != null){
                req.addBodyParameter("email", email);
            }
            if(password != null){
                req.addBodyParameter("password", password);
            }
            req
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .addHeaders("api-key", "VmRRL9EiqQ5Sf4VuHXGF")
                    .build().getAsJSONObject((new JSONObjectRequestListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("external update profile success", response.toString());
                    // do anything with response
                    try {
                        int status = response.getInt("status");

                        if(status == 200){
                            JSONObject userJO = response.getJSONObject("data");
                            String email = userJO.getString("email");
                            String username = userJO.has("displayName")?userJO.getString("displayName"):email;
                            String uid = userJO.getString("uid");
                            user = new LoggedInUser(
                                    uid, username, email);
                            success.accept(new Result.Success<>(user));

                            //updates parse server
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
                            query.whereEqualTo("email", email);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        //Object was successfully retrieved
                                        // Update the fields we want to
                                        ParseObject object = objects.get(0);
                                        if (firstName != null){
                                            object.put("firstName", firstName);
                                        }
                                        if(lastName != null){
                                            object.put("lastName", lastName);
                                        }
                                        if(location != null){
                                            object.put("location", location);
                                        }
                                        if(bio != null){
                                            object.put("bio", bio);
                                        }
                                        if(gender != null){
                                            object.put("gender", gender);
                                        }
                                        if(email != null){
                                            object.put("email", email);
                                        }
                                        if(bitmap != null){
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            bitmap.recycle();
                                            object.put("pic", new ParseFile("pic.PNG", byteArray));
                                        }


                                        //All other fields will remain the same
                                        object.saveInBackground();

                                    } else {
                                        // something went wrong
                                    }
                                }
                            });

                        }
                        else{
                            JSONObject respError = response.getJSONObject("error");
                            error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error.accept(new Result.Error("json-error",new Exception(e.getMessage())));
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onError(ANError e) {
                    // handle error
                    Log.e("update profile", e.getMessage());
                    Log.e("update profile error", e.getErrorBody());

                    try {
                        JSONObject jo = new JSONObject(e.getErrorBody());
                        error.accept(new Result.Error(jo.getJSONObject("error").getString("code"),new Exception(jo.getJSONObject("error").getString("message"))));
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }));
        } catch (Exception e) {
            error.accept(new Result.Error("unknown", new IOException("Error registering", e)));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {


        try {
            AndroidNetworking.post("https://class.whattheduck.app/api/login")
                    .addBodyParameter("email", username)
                    .addBodyParameter("password", password)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .addHeaders("api-key", "VmRRL9EiqQ5Sf4VuHXGF")
                    .build().getAsJSONObject((new JSONObjectRequestListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("external login success", response.toString());
                    // do anything with response
                    try {
                        int status = response.getInt("status");

                        if(status == 200){
                            JSONObject userJO = response.getJSONObject("data");
                            String email = userJO.getString("email");
                            String username = userJO.has("displayName")?userJO.getString("displayName"):email;
                            String uid = userJO.getString("uid");
                            user = new LoggedInUser(
                                    uid, username, email);
                            success.accept(new Result.Success<>(user));
                        }
                        else{
                            JSONObject respError = response.getJSONObject("error");
                            error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error.accept(new Result.Error("json-error",new Exception(e.getMessage())));
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onError(ANError e) {
                    // handle error

                    Log.e("external login error", e.getErrorBody());

                    try {
                        JSONObject jo = new JSONObject(e.getErrorBody());
                        error.accept(new Result.Error(jo.getJSONObject("error").getString("code"),new Exception(jo.getJSONObject("error").getString("message"))));
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            }));
        } catch (Exception e) {
            error.accept(new Result.Error("unknown", new IOException("Error registering", e)));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String username, String lastName, String location,
                         String bio, Bitmap pic, String gender, String password,
                         Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {

        //check if user exists already
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.v("Login", "Query to find if user exists successful");

                    //email already registered, do not log in
                    if (objects.size() != 0) {

                        Log.v("Login", "User already exists error");
                        error.accept(new Result.Error("User already exists", new Exception("ExistingUser")));
                        return;
                    } else {
                        finishRegister(email, username, lastName, location, bio, pic, gender, password,
                                success, error);
                    }

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void finishRegister(String email, String username, String lastName, String location,
                               String bio, Bitmap pic, String gender, String password,
                               Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {

        try {
            AndroidNetworking.post("https://class.whattheduck.app/api/register")
                    .addBodyParameter("username", username)
                    .addBodyParameter("email", email)
                    .addBodyParameter("password", password)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .addHeaders("api-key", "VmRRL9EiqQ5Sf4VuHXGF")
                    .build().getAsJSONObject((new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        if(status == 200) {

                            Log.v("register", "email is " + email);

                            user = new LoggedInUser(
                                    response.getJSONObject("data").getString("uid"), response.getJSONObject("data").getString("displayName"), response.getJSONObject("data").getString("email"));
                            success.accept(new Result.Success<>(user));

                            Log.v("register", "success with the api!");

                            //create parse entity and places info in it
                            ParseObject entity = new ParseObject("Duck");
                            entity.put("firstName", username);
                            entity.put("lastName", lastName);
                            entity.put("location", location);
                            entity.put("bio", bio);

                            //converts bitmap to file for parse
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            entity.put("pic", new ParseFile("pic.PNG", byteArray));

                            //continues putting info
                            entity.put("gender", gender);
                            entity.put("email", email);

                            //inserts into table
                            entity.saveInBackground(e -> {
                                if (e==null){
                                    //Save was done
                                    Log.v("Login", "Data has been saved in Parse!");
                                }else{
                                    //Something went wrong
                                    Log.e("Login", "Data unsuccessfully saved");
                                    return;
                                }
                            });

                        }
                        else{
                            JSONObject respError = response.getJSONObject("error");
                            error.accept(new Result.Error(respError.getString("code"), new Exception(respError.getString("message"))));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error.accept(new Result.Error("json-error",e));
                    }
                }

                @Override
                public void onError(ANError e) {
                    // handle error
                    error.accept(new Result.Error("an-error", new Exception(e.getErrorBody())));
                }
            }));
        } catch (Exception e) {
            Log.v("register", e.getMessage());
            error.accept(new Result.Error("unknown", new IOException("Error registering", e)));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

package com.example.duckdating.data;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.example.duckdating.ui.match.Message;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

import okhttp3.OkHttpClient;

public class ParseApplication extends Application{
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);

        // Use for monitoring Parse OkHttp trafic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("dH97TkJ8DCOCoNjr5CMwL1kHUQU6xCAMSQlEWPEP")
                .clientKey("PoPd8t23AxX2SzFbsiUV87m9dV0JOS1ibZvRGXHn")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

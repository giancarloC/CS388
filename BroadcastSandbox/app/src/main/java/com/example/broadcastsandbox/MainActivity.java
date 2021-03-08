package com.example.broadcastsandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction("com.example.broadcastsandbox.TEST_NOTIFICATION");
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        this.registerReceiver(br, filter);
    }

    public void onClick(View view){
        Intent intent = new Intent();
        intent.setAction("com.example.broadcastsandbox.TEST_NOTIFICATION");
        intent.putExtra("data", "Nothing to see here, move along.");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (br != null){
            unregisterReceiver(br);
        }
    }
}
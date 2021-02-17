package com.example.clicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button myButton;
    TextView myText, timerText;
    int clicks = 0;
    int time = 10;
    ScheduledExecutorService service;
    ScheduledFuture timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myButton = findViewById(R.id.button);
        myText = findViewById(R.id.textView);
        timerText = findViewById(R.id.timerText);
        service = Executors.newScheduledThreadPool(1);
    }

    public void onClick(View view){
        if (timer == null){
            clicks = 0;
            time = 10;
            startTimer();
        }
        clicks++;
        myText.setText("Clicks: " + clicks);

    }

    void startTimer(){
        timer = service.scheduleAtFixedRate(() -> {
            try {
                //here is what will trigger every second
                runOnUiThread(() -> timerText.setText("Time Remaining: " + time));
                time--;
                if (time <= 0) {
                    if (timer != null) {
                        timer.cancel(true);
                        timer = null;
                    }

                    //disables button
                    runOnUiThread(() ->{
                        myButton.setEnabled(false);
                    });

                    //enable button after 3 seconds
                    service.schedule(() -> runOnUiThread(() ->{
                        myButton.setEnabled(true);
                    }),3, TimeUnit.SECONDS);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
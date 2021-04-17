package com.example.whackamole;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whackamole.db.entity.ScoreEntity;
import com.example.whackamole.db.viewmodel.ScoreViewModel;
import com.example.whackamole.db.viewmodel.ScoreViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity {
    TextView scoreText, timeText;
    LinearLayout buttonContainer;
    GridLayout grid;
    final static int totalButtons = 9;
    final static int columns = 3;
    boolean ongoing = false;
    List<Integer> sync = new ArrayList<Integer>();
    int score = 0;
    long time = 0;
    final int REWARD = 10;
    final String TAG = "WhackAMole";
    final int MARGIN = 5;

    ExecutorService executor = Executors.newScheduledThreadPool(1000);

    private ScoreViewModel scoreViewModel;

    CountDownTimer cTimer = null;

    //start timer function
    void startGame() {

        grid.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);

        ongoing = true;

        executor.submit(()->{
            runOnUiThread(()->{
                cTimer.start();
            });

        });



        for (int i = 0; i < totalButtons; i++){
            int finalI = i;
            executor.submit(() -> {
                Button b = findViewById(finalI);
                Random rn = new Random();
                int chance;
                while(ongoing) {
                    chance = rn.nextInt(10) + 1; //random num from 1 to 10
                    if (chance < 4) { //30% chance to spawn
                        b.setText("X");
                        try {
                            TimeUnit.MILLISECONDS.sleep(2000); //waits two seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        b.setText("");
                    }
                    else { //waits a second
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i(TAG, "button complete " + finalI);
                }
                Log.i(TAG, "button: " + finalI);
            });
        }

        Log.v(TAG, "startGame()");
        updateUI();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        buttonContainer = findViewById(R.id.buttonContainer);
        grid = findViewById(R.id.grid);
        scoreText = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);
        buildGrid();
        resetButtons();
        updateUI();
/*
        saveScore("first", 1);
        saveScore("second", 1);
        saveScore("third", 1);
        saveScore("fourth", 1);
        saveScore("fifth", 1);
        saveScore("sixth", 1);
        saveScore("seventh", 1);
        saveScore("eight", 1);
        saveScore("ninth", 1);
        saveScore("tenth", 1);
        saveScore("eleventh", 1);
*/

        for (int i = 0; i < totalButtons; i++){
            sync.add(1); //adds true
        }

        cTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished/1000;
                timeText.setText("Time left: " + time);
            }
            public void onFinish() {
                ongoing = false;
                promptUser("Time ran up!", "Type in your name to record your score", true, (x) -> {
                    //TODO actually save score/name but this happens in the dialog
                    //using null value for a canceled prompt, otherwise will be a string
                    if(x != null) {
                        Log.v(TAG, x.toString());
                        final int nScore = score;
                        saveScore(x.toString(), nScore);
                    }
                    newGame();
                });
            }
        };

        newGame();
    }



    //https://developer.android.com/training/appbar/action-views
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.game:
                // do something
                cancelTimer();
                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.scores:
                // do something
                cancelTimer();
                intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.quit:
                newGame();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    void resetButtons() {
        for (int i = 0; i < totalButtons; i++) {
            ((Button) findViewById(i)).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    //http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog.html
    void promptUser(String title, String message, boolean expectsInput, Consumer callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(message);
        EditText input = null;
        if (expectsInput) {
            // Set an EditText view to get user input
            input = new EditText(this);
            alert.setView(input);
        }

        EditText finalInput = input;
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = "-";
                if (expectsInput) {
                    value = finalInput.getText().toString();
                    Log.v(TAG, "WE got it!!! " + value);
                    // Do something with value!
                }
                if (callback != null) {
                    callback.accept(value);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Log.v(TAG, "They want out");
                if (callback != null) {
                    callback.accept(null);
                }
            }
        });

        alert.show();
    }

    //all the buttons
    public void onClickStartGame(View view){
        startGame();
    }
    public void onClickScores(View view){
        Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
        startActivity(intent);
        finish();
    }
    public void onClickHistory(View view){
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }
    public void onClickSettings(View view){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    void newGame() {
        cTimer.cancel();
        score = 0;
        time = 0;
        updateUI();
        grid.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
    }

    //https://mkyong.com/java/java-generate-random-integers-in-a-range/
    private static int getRandom(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    void updateUI() {
        scoreText.setText("Score: " + score);
        timeText.setText("Time left: " + time);
    }

    void buildGrid() {
        for (int i = 0; i < totalButtons; i++) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(
                    this,
                    R.style.Button_White
            );

            Button btnTag = new Button(newContext);

            btnTag.setId(i);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;

            param.rightMargin = MARGIN;
            param.topMargin = MARGIN;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(i / columns);
            param.rowSpec = GridLayout.spec(i % columns);
            btnTag.setLayoutParams(param);
            btnTag.setOnClickListener(this::onClickityClackity);
            //add button to the layout
            grid.addView(btnTag);
        }
    }
    void saveScore(String name, int score){
        if(scoreViewModel == null){
            scoreViewModel = new ViewModelProvider(this, new ScoreViewModelFactory(getApplication())).get(ScoreViewModel.class);
        }
        ScoreEntity user = new ScoreEntity(name, score);
        scoreViewModel.insert(user);
    }
    void onClickityClackity(View view) {
        Log.v(TAG, "clicked " + view.getId());
        final int id = view.getId();
        Button b = findViewById(id);

        Log.v(TAG, b.getText().toString());
        if (b.getText().toString() == "X" && b.getTextScaleX() == 1){
            executor.submit(()->{
                runOnUiThread(() -> {
                    b.setTextScaleX(0);
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(2000); //waits two seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                b.setTextScaleX(1);

            });
            score++;
        }
        updateUI();
    }
    public void quit(View view){
        finish();
        System.exit(0);
    }
}
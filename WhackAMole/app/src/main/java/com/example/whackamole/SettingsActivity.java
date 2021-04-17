package com.example.whackamole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whackamole.db.entity.ScoreEntity;
import com.example.whackamole.db.viewmodel.ScoreViewModel;
import com.example.whackamole.db.viewmodel.ScoreViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {
    public String TAG = "SettingsActivity";
    ScoreListAdapter adapter;
    Button deleteBtn;
    List<ScoreEntity> allScores;

    private ScoreViewModel scoreViewModel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        deleteBtn = findViewById(R.id.deleteBtn);


        adapter = new ScoreListAdapter(new ScoreListAdapter.UserDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Since our model has a 1 parameter constructor we need to use a factory to map it
        scoreViewModel = new ViewModelProvider(this, new ScoreViewModelFactory(getApplication())).get(ScoreViewModel.class);
        scoreViewModel.getTop().observe(this, scores -> {
            allScores = scores;
            adapter.submitList(allScores);
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        return;
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //delete item
                        Log.v(TAG, "Long pressed item, about to delete");
                        allScores.remove(position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemRemoved(position);
                            }
                        });
                    }

                })
        );

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
            }
        });
    }

    public void deleteAll(){
        allScores.clear();
        adapter.submitList(allScores);
        adapter.notifyDataSetChanged();


        Single.fromCallable(() -> scoreViewModel.deleteAll()) //User Dao fun
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();




        //adapter.notifyDataSetChanged();

        //adapter.submitList(allScores);
        //adapter.notifyDataSetChanged();
        //scoreViewModel.deleteAll().observe(this, );
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
                intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.scores:
                // do something
                intent = new Intent(SettingsActivity.this, ScoreActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteScore(View view, String name, int score, String date){
        scoreViewModel.deleteScore(name, score, date);

        adapter.notifyDataSetChanged();
    }
}



package com.example.whackamole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class HistoryActivity extends AppCompatActivity {
    public String TAG = "HistoryActivity";
    private int page = 1;
    private int totalScores, pages;
    List<ScoreEntity> tenScores = new ArrayList<>();
    List<ScoreEntity> allScores = new ArrayList<>();
    ScoreListAdapter adapter;
    Button next, back;
    TextView pageNum;


    private ScoreViewModel scoreViewModel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        pageNum = findViewById(R.id.pageNum);
        pageNum.setText("Page: 1");

        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page >= pages){
                    return;
                }

                page += 1;
                tenScores.clear();
                int base = (page-1)*10;
                for(int i = base; i < base + 10 && i < totalScores; i++){
                    tenScores.add(allScores.get(i));
                }

                adapter.submitList(tenScores);
                pageNum.setText("Page: " + page);
                adapter.notifyDataSetChanged();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page == 1){
                    return;
                }

                page -= 1;
                tenScores.clear();
                int end = (page)*10;
                for(int i = end-10; i < end && i < totalScores; i++){
                    tenScores.add(allScores.get(i));
                }

                adapter.submitList(tenScores);
                pageNum.setText("Page: " + page);
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ScoreListAdapter(new ScoreListAdapter.UserDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Since our model has a 1 parameter constructor we need to use a factory to map it
        scoreViewModel = new ViewModelProvider(this, new ScoreViewModelFactory(getApplication())).get(ScoreViewModel.class);
        scoreViewModel.getTop().observe(this, scores -> {
            allScores = scores;

            //find overall amount of pages
            totalScores = scores.size();
            pages = (int) Math.ceil(totalScores/10);

            // Update the cached copy of the words in the adapter.
            for (int i = 0; i < totalScores && i < 10; i++){
                tenScores.add(scores.get(i));
            }
            adapter.submitList(tenScores);
        });
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
                intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.scores:
                // do something
                intent = new Intent(HistoryActivity.this, ScoreActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}



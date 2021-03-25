package com.example.roomssandbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.roomssandbox.db.entity.UserEntity;
import com.example.roomssandbox.viewmodel.UserViewModel;
import com.example.roomssandbox.viewmodel.UserViewModelFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private UserViewModel userViewModel;
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 1;
    private Button btnFilter;
    private EditText editFilter;
    private TextView textCount;
    private int totalCount;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editFilter = findViewById(R.id.editFilter);
        btnFilter = findViewById(R.id.btnFilter);
        textCount = findViewById(R.id.textCount);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final UserListAdapter adapter = new UserListAdapter(new UserListAdapter.UserDiff(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Since our model has a 1 parameter constructor we need to use a factory to map it
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(getApplication())).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, users -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(users);
            count = users.size();
            totalCount = count;
            textCount.setText(Integer.toString(count) + " out of " + Integer.toString(totalCount));
        });

        //listener for filter button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editFilter.getText().toString();

                //checks if its empty
                if (text.matches("")){
                    Toast.makeText(MainActivity.this, "Text is Empty", Toast.LENGTH_SHORT).show();
                    userViewModel.getAllUsers().observe(MainActivity.this, users -> {
                        // Update the cached copy of the words in the adapter.
                        adapter.submitList(users);
                        count = users.size();
                        totalCount = count;
                        textCount.setText(Integer.toString(count) + " out of " + Integer.toString(totalCount));
                    });
                    return;
                }

                String first, last;
                String[] name = text.split(" ", 2);
                first = name[0];
                if (name.length == 1){
                    last = "";
                }
                else{
                    last = name[1];
                }

                LiveData<List<UserEntity>> new_users = userViewModel.getFilteredUsers(first, last);
                if (new_users == null){
                    Toast.makeText(MainActivity.this, "Found no matches", Toast.LENGTH_SHORT).show();
                }
                else{
                    new_users.observe(MainActivity.this, users -> {
                        // Update the cached copy of the words in the adapter.
                        adapter.submitList(users);
                        count = users.size();
                        textCount.setText(Integer.toString(count) + " out of " + Integer.toString(totalCount));
                    });
                }

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            startActivityForResult(intent, NEW_USER_ACTIVITY_REQUEST_CODE);
        });
    }

    public void deleteInMain(UserEntity user){
        userViewModel.delete(user);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String[] n = data.getStringArrayExtra(AddUserActivity.EXTRA_REPLY);
            UserEntity user = new UserEntity(n[1], n[0]);
            userViewModel.insert(user);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}
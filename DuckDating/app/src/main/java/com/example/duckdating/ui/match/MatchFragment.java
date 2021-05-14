package com.example.duckdating.ui.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duckdating.R;
import com.example.duckdating.ui.home.HomeViewModel;
import com.example.duckdating.ui.home.HomeViewModelFactory;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.List;

public class MatchFragment extends Fragment {
    private static final String TAG = "MatchFragment";

    private MatchViewModel matchViewModel;
    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView title, emailText;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        matchViewModel = new ViewModelProvider(this, new MatchViewModelFactory(getActivity().getApplication())).get(MatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_matches, container, false);
        title = root.findViewById(R.id.rvTitle);
        emailText = root.findViewById(R.id.email);
        context = getActivity();

        //sets up recycler view
        recyclerView = root.findViewById(R.id.rvMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyRecyclerViewAdapter(context);
        recyclerView.setAdapter(adapter);

        //populates email
        matchViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {emailText.setText(email);});

        //populate recycler view with live data
        matchViewModel.getListDucks().observe(getViewLifecycleOwner(), listDucks -> {
            //if there are none, change the text
            if (listDucks.size() == 0){
                title.setText("No Matches Yet!");
                Log.v(TAG, "No matches :(");
            }
            else{
                adapter.setList(listDucks);

                //creates click listener to go to profile
                adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ParseObject matchObject = listDucks.get(position);

                        String name = matchObject.getString("firstName") + " " + matchObject.getString("lastName");
                        String location = matchObject.getString("location");
                        String gender = matchObject.getString("gender");
                        String bio = matchObject.getString("bio");
                        String matchEmail = matchObject.getString("email");

                        ParseFile image = matchObject.getParseFile("pic");
                        image.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Intent intent = new Intent(getActivity(), MatchProfileActivity.class);
                                intent.putExtra("imageBytes", data);
                                intent.putExtra("name", name);
                                intent.putExtra("location", location);
                                intent.putExtra("gender", gender);
                                intent.putExtra("bio", bio);
                                intent.putExtra("email", emailText.getText());
                                intent.putExtra("matchEmail", matchEmail);
                                startActivity(intent);
                            }
                        });
                    }
                });

                title.setText("List of Matches");
                Log.v(TAG, "Matches found! " + listDucks.get(0).get("email"));
            }
        });



        return root;
    }
}
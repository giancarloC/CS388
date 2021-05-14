package com.example.duckdating.ui.match;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duckdating.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = "ChatActivity";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    boolean mFirstLoad;
    EditText etMessage;
    ImageButton btSend;

    String email, matchEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get user duck information from intent
        email = getIntent().getStringExtra("email");
        matchEmail = getIntent().getStringExtra("matchEmail");

        //setup views
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        rvChat = findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        mAdapter = new ChatAdapter(ChatActivity.this, email, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        refreshMessages();

        //when button is pressed, send to parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.setBody(data);
                message.setEmail(email);
                message.setMatchEmail(matchEmail);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Toast.makeText(ChatActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                            refreshMessages();
                        }
                        else{
                            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                etMessage.setText(null);
            }
        });
    }

    //queries messages from parse
    void refreshMessages() {

        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo("email", email);
        query.whereEqualTo("matchEmail", matchEmail);

        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);
        query2.whereEqualTo("email", matchEmail);
        query2.whereEqualTo("matchEmail", email);

        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();
        queries.add(query);
        queries.add(query2);

        ParseQuery<Message> query3 = ParseQuery.or(queries);
        query3.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        // get the latest 50 messages, order will show up newest to oldest of this group
        query3.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query3.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    Log.v("messages", "length: " + messages.size());
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }
}

package com.example.duckdating.ui.match;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duckdating.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private String email;

    private static final int MESSAGE_OUTGOING = 69;
    private static final int MESSAGE_INCOMING = 420;


    public ChatAdapter(Context context, String email, List<Message> messages) {
        mMessages = messages;
        this.email = email;
        mContext = context;
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getEmail() != null && message.getEmail().equals(email);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isMe(position)){
            return MESSAGE_OUTGOING;
        }
        else{
            return MESSAGE_INCOMING;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder{
        public MessageViewHolder(@NonNull View itemView){
            super(itemView);
        }

        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body, name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            name = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bindMessage(Message message) {
            //sets body
            body.setText(message.getBody());

            //sets image
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
            query.whereEqualTo("email", message.getEmail());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        ParseObject o = objects.get(0);

                        //grabs name
                        String fullName = o.getString("firstName") + " " + o.getString("lastName");
                        name.setText(fullName);

                        //grabs picture
                        ParseFile file = o.getParseFile("pic");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageOther.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });

        }
    }


    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            //sets body
            body.setText(message.getBody());

            //sets image
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Duck");
            query.whereEqualTo("email", message.getEmail());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null){
                        ParseFile file = objects.get(0).getParseFile("pic");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageMe.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
        }
    }


}


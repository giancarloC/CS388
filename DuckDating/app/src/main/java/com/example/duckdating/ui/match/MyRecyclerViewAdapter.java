package com.example.duckdating.ui.match;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duckdating.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<ParseObject> mList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = new ArrayList<ParseObject>(); //might not be needed
    }

    public void setList(List<ParseObject> list){
        if (mList == null){
            mList = list;
            notifyItemRangeInserted(0, list.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mList.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mList.get(oldItemPosition).getObjectId() == list.get(newItemPosition).getObjectId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mList.get(oldItemPosition).get("email").toString().equals(
                            list.get(newItemPosition).get("email").toString());
                }
            });
            mList = list;
            result.dispatchUpdatesTo(this);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject duck = mList.get(position);

        holder.matchName.setText(duck.get("firstName").toString() + " " + duck.get("lastName").toString());
        holder.matchLocation.setText(duck.get("location").toString());

        //sets up image
        ParseFile imageFile = (ParseFile) duck.get("pic");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                holder.matchImage.setImageBitmap(bmp);
            }
        });



    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView matchImage;
        TextView matchName, matchLocation;

        ViewHolder(View itemView) {
            super(itemView);
            matchImage = itemView.findViewById(R.id.matchImage);
            matchName = itemView.findViewById(R.id.matchName);
            matchLocation = itemView.findViewById(R.id.matchLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ParseObject getItem(int id) {
        return mList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


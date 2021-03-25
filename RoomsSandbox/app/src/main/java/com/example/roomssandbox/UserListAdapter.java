package com.example.roomssandbox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.roomssandbox.db.entity.UserEntity;

public class UserListAdapter extends ListAdapter<UserEntity, UserViewHolder> {
    private Context mContext;


    public UserListAdapter(@NonNull DiffUtil.ItemCallback<UserEntity> diffCallback, Context context) {
        super(diffCallback);
        this.mContext = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return UserViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserEntity current = getItem(position);
        holder.bind(current.firstName, current.lastName);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity){
                    ((MainActivity)mContext).deleteInMain(current);
                }
            }
        });
    }

    static class UserDiff extends DiffUtil.ItemCallback<UserEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
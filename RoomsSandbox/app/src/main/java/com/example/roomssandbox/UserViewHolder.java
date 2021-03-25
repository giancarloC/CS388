package com.example.roomssandbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final TextView firstNameView, lastNameView;
    final Button btnDelete;

    private UserViewHolder(View itemView) {
        super(itemView);
        firstNameView = itemView.findViewById(R.id.firstNameView);
        lastNameView = itemView.findViewById(R.id.lastNameview);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }

    public void bind(String firstName, String lastName) {
        firstNameView.setText(firstName);
        lastNameView.setText(lastName);
    }

    static UserViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new UserViewHolder(view);
    }
}

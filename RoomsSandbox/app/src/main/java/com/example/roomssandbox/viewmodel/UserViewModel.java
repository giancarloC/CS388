package com.example.roomssandbox.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.roomssandbox.db.UserRepository;
import com.example.roomssandbox.db.entity.UserEntity;

public class UserViewModel extends AndroidViewModel {

    private UserRepository mRepository;

    private final LiveData<List<UserEntity>> allUsers;
    private LiveData<List<UserEntity>> filteredUsers;

    public UserViewModel(Application application) {
        super(application);
        mRepository = new UserRepository(application);
        allUsers = mRepository.getAllUsers();
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }

    public int getAllUserCount(){
        return allUsers.getValue().size();
    }

    public LiveData<List<UserEntity>> getFilteredUsers(String first, String last) {
        filteredUsers = mRepository.getFilteredUsers(first, last);
        return filteredUsers;
    }

    public void insert(UserEntity user) {
        mRepository.insert(user);
    }

    public void delete(UserEntity user) {
        mRepository.delete(user);
    }
}

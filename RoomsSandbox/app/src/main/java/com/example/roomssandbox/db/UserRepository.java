package com.example.roomssandbox.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.roomssandbox.db.entity.UserEntity;
import com.example.roomssandbox.db.dao.UserDao;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<UserEntity>> allUsers;
    private LiveData<List<UserEntity>> filteredUsers;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }

    //grab filtered users
    public LiveData<List<UserEntity>> getFilteredUsers(String first, String last) {
        filteredUsers = userDao.findByName(first, last);
        return filteredUsers;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(UserEntity user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public void delete(UserEntity user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }
}

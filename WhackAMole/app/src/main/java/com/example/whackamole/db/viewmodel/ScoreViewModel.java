package com.example.whackamole.db.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.whackamole.db.ScoreRepository;
import com.example.whackamole.db.entity.ScoreEntity;

public class ScoreViewModel extends AndroidViewModel {

    private ScoreRepository mRepository;

    private final LiveData<List<ScoreEntity>> top10;

    public ScoreViewModel(Application application) {
        super(application);
        mRepository = new ScoreRepository(application);
        top10 = mRepository.getTop10();
    }

    public LiveData<List<ScoreEntity>> getTop10() {
        return top10;
    }

    public void insert(ScoreEntity score) {
        mRepository.insert(score);
    }
}
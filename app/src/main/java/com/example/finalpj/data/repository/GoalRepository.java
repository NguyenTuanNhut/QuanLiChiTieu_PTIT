package com.example.finalpj.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.GoalDao;
import com.example.finalpj.data.db.entity.Goal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private GoalDao goalDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GoalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        goalDao = db.goalDao();
    }

    public void insert(Goal goal) {
        executor.execute(() -> goalDao.insert(goal));
    }

    public void update(Goal goal) {
        executor.execute(() -> goalDao.update(goal));
    }

    public void delete(Goal goal) {
        executor.execute(() -> goalDao.delete(goal));
    }

    public LiveData<List<Goal>> getAllGoals(int userId) {
        return goalDao.getAllGoals(userId);
    }
}

package com.example.finalpj.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.BudgetDao;
import com.example.finalpj.data.db.entity.Budget;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {
    private BudgetDao budgetDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        budgetDao = db.budgetDao();
    }

    public void insert(Budget budget) {
        executor.execute(() -> budgetDao.insert(budget));
    }

    public void update(Budget budget) {
        executor.execute(() -> budgetDao.update(budget));
    }

    public void delete(Budget budget) {
        executor.execute(() -> budgetDao.delete(budget));
    }

    public LiveData<Budget> getTotalBudget(int month, int year) {
        return budgetDao.getTotalBudget(month, year);
    }

    public LiveData<Budget> getCategoryBudget(int month, int year, int categoryId) {
        return budgetDao.getCategoryBudget(month, year, categoryId);
    }

    public LiveData<List<Budget>> getAllBudgetsByMonth(int month, int year) {
        return budgetDao.getAllBudgetsByMonth(month, year);
    }
}

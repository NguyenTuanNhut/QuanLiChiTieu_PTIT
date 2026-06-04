package com.example.finalpj.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.RecurringTransactionDao;
import com.example.finalpj.data.db.entity.RecurringTransaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecurringTransactionRepository {
    private RecurringTransactionDao recurringTransactionDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public RecurringTransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        recurringTransactionDao = db.recurringTransactionDao();
    }

    public void insert(RecurringTransaction recurringTransaction) {
        executor.execute(() -> recurringTransactionDao.insert(recurringTransaction));
    }

    public void update(RecurringTransaction recurringTransaction) {
        executor.execute(() -> recurringTransactionDao.update(recurringTransaction));
    }

    public void delete(RecurringTransaction recurringTransaction) {
        executor.execute(() -> recurringTransactionDao.delete(recurringTransaction));
    }

    public LiveData<List<RecurringTransaction>> getActiveRecurring() {
        return recurringTransactionDao.getActiveRecurring();
    }

    public List<RecurringTransaction> getAllSync() {
        return recurringTransactionDao.getAllSync();
    }
}

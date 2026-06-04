package com.example.finalpj.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.AccountDao;
import com.example.finalpj.data.db.entity.Account;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {
    private AccountDao accountDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        accountDao = db.accountDao();
    }

    public void insert(Account account) {
        executor.execute(() -> accountDao.insert(account));
    }

    public void update(Account account) {
        executor.execute(() -> accountDao.update(account));
    }

    public void delete(Account account) {
        executor.execute(() -> accountDao.delete(account));
    }

    public LiveData<List<Account>> getAll() {
        return accountDao.getAll();
    }

    public List<Account> getAllSync() {
        return accountDao.getAllSync();
    }
}

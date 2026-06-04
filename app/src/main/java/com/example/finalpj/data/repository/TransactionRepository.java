package com.example.finalpj.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.TransactionDao;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private TransactionDao transactionDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        transactionDao = db.transactionDao();
    }

    // Thêm giao dịch (chạy trên background thread)
    public void insert(Transaction transaction) {
        executor.execute(() -> transactionDao.insert(transaction));
    }

    // Xóa giao dịch
    public void delete(Transaction transaction) {
        executor.execute(() -> transactionDao.delete(transaction));
    }

    public void update(Transaction transaction) {
        executor.execute(() -> transactionDao.update(transaction));
    }

    public LiveData<Transaction> getById(int id) {
        return transactionDao.getById(id);
    }

    // Lấy giao dịch theo tháng
    public LiveData<List<TransactionWithCategory>> getByMonth(String month, String year) {
        return transactionDao.getByMonth(month, year);
    }

    // Tổng chi tiêu tháng
    public LiveData<Double> getTotalExpense(String monthYear) {
        return transactionDao.getTotalExpense(monthYear);
    }

    // Tổng thu nhập tháng
    public LiveData<Double> getTotalIncome(String monthYear) {
        return transactionDao.getTotalIncome(monthYear);
    }

    // Derived balance
    public LiveData<Double> getBalance(String monthYear) {
        return transactionDao.getBalance(monthYear);
    }

    // Chi tiêu theo danh mục
    public LiveData<List<com.example.finalpj.data.db.entity.CategoryExpense>> getExpenseByCategory(String monthYear) {
        return transactionDao.getExpenseByCategory(monthYear);
    }

    // Tìm kiếm giao dịch
    public LiveData<List<TransactionWithCategory>> search(String query) {
        return transactionDao.searchTransactions(query);
    }

    public LiveData<List<TransactionWithCategory>> getAll() {
        return transactionDao.getAllTransactions();
    }
}

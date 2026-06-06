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

/**
 * Lớp Repository đóng vai trò là tầng trung gian điều phối dữ liệu.
 * Giúp tách biệt logic truy cập dữ liệu (Database) khỏi Logic nghiệp vụ (ViewModel).
 */
public class TransactionRepository {
    private TransactionDao transactionDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(Application application) {
        // Lấy instance của Database và DAO
        AppDatabase db = AppDatabase.getInstance(application);
        transactionDao = db.transactionDao();
    }

    /**
     * Thêm giao dịch mới. 
     * Lưu ý: Mọi thao tác ghi vào DB (Insert/Update/Delete) phải chạy trên luồng phụ (Background thread).
     */
    public void insert(Transaction transaction) {
        executor.execute(() -> transactionDao.insert(transaction));
    }

    // Xóa giao dịch (chạy ngầm)
    public void delete(Transaction transaction) {
        executor.execute(() -> transactionDao.delete(transaction));
    }

    // Cập nhật giao dịch (chạy ngầm)
    public void update(Transaction transaction) {
        executor.execute(() -> transactionDao.update(transaction));
    }

    // Lấy giao dịch cụ thể theo ID
    public LiveData<Transaction> getById(int id) {
        return transactionDao.getById(id);
    }

    // Lấy danh sách giao dịch kèm danh mục theo tháng/năm
    public LiveData<List<TransactionWithCategory>> getByMonth(String month, String year, int userId) {
        return transactionDao.getByMonth(month, year, userId);
    }

    // Lấy tổng chi tiêu của tháng
    public LiveData<Double> getTotalExpense(String monthYear, int userId) {
        return transactionDao.getTotalExpense(monthYear, userId);
    }

    // Lấy tổng thu nhập của tháng
    public LiveData<Double> getTotalIncome(String monthYear, int userId) {
        return transactionDao.getTotalIncome(monthYear, userId);
    }

    // Lấy số dư hiện tại trong tháng
    public LiveData<Double> getBalance(String monthYear, int userId) {
        return transactionDao.getBalance(monthYear, userId);
    }

    // Lấy dữ liệu thống kê chi tiêu theo danh mục
    public LiveData<List<com.example.finalpj.data.db.entity.CategoryExpense>> getExpenseByCategory(String monthYear, int userId) {
        return transactionDao.getExpenseByCategory(monthYear, userId);
    }

    // Tìm kiếm giao dịch
    public LiveData<List<TransactionWithCategory>> search(String query, int userId) {
        return transactionDao.searchTransactions(query, userId);
    }

    // Lấy toàn bộ lịch sử giao dịch
    public LiveData<List<TransactionWithCategory>> getAll(int userId) {
        return transactionDao.getAllTransactions(userId);
    }

    public List<TransactionWithCategory> getPaged(int userId, int limit, int offset) {
        return transactionDao.getTransactionsPaged(userId, limit, offset);
    }

    public LiveData<Integer> getCount(int userId) {
        return transactionDao.getTransactionCount(userId);
    }
}

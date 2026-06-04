package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.CategoryExpense;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;

import java.util.List;

@Dao
public interface TransactionDao {

        // Thêm giao dịch mới
        @Insert
        void insert(Transaction transaction);

        // Xóa giao dịch
        @Delete
        void delete(Transaction transaction);

        // Cập nhật
        @Update
        void update(Transaction transaction);

        // Lấy tất cả giao dịch theo tháng (mới nhất trước)
        @Query("SELECT * FROM transactions " +
                        "WHERE strftime('%m', date/1000, 'unixepoch') = :month " +
                        "AND strftime('%Y', date/1000, 'unixepoch') = :year " +
                        "ORDER BY date DESC")
        LiveData<List<TransactionWithCategory>> getByMonth(String month, String year);

        // Lấy giao dịch theo ID
        @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
        LiveData<Transaction> getById(int id);

        // Tổng thu nhập trong tháng
        @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE type = 'INCOME' " +
                        "AND strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear")
        LiveData<Double> getTotalIncome(String monthYear);

        // Tổng chi tiêu trong tháng
        @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE type = 'EXPENSE' " +
                        "AND strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear")
        LiveData<Double> getTotalExpense(String monthYear);

        // Derived balance: tổng thu nhập - tổng chi tiêu trong tháng
        @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) " +
                        "FROM transactions " +
                        "WHERE strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear")
        LiveData<Double> getBalance(String monthYear);

        // Chi tiêu theo từng danh mục (cho biểu đồ)
        @Query("SELECT c.name AS categoryName, SUM(t.amount) AS total " +
                        "FROM transactions t " +
                        "INNER JOIN categories c ON t.category_id = c.id " +
                        "WHERE t.type = 'EXPENSE' " +
                        "AND strftime('%m-%Y', t.date/1000, 'unixepoch') = :monthYear " +
                        "GROUP BY c.id")
        LiveData<List<CategoryExpense>> getExpenseByCategory(String monthYear);

        // Lấy tất cả giao dịch (cho việc tìm kiếm thông minh ở ViewModel)
        @Query("SELECT t.* FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.id " +
                "ORDER BY t.date DESC")
        LiveData<List<TransactionWithCategory>> getAllTransactions();

        // Tìm kiếm giao dịch tương đối
        @Query("SELECT t.* FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.id " +
                "WHERE t.note LIKE '%' || :query || '%' " +
                "OR c.name LIKE '%' || :query || '%' " +
                "ORDER BY t.date DESC")
        LiveData<List<TransactionWithCategory>> searchTransactions(String query);

}

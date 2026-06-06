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

/**
 * Interface định nghĩa các câu lệnh SQL để tương tác với bảng transactions.
 * Sử dụng thư viện Room để tự động sinh mã thực thi.
 */
@Dao
public interface TransactionDao {

    // Thêm một giao dịch mới vào Database
    @Insert
    void insert(Transaction transaction);

    // Xóa một giao dịch hiện có
    @Delete
    void delete(Transaction transaction);

    // Cập nhật thông tin giao dịch
    @Update
    void update(Transaction transaction);

    /**
     * Lấy danh sách giao dịch kèm theo thông tin danh mục theo tháng và năm.
     * Trả về LiveData để tự động cập nhật UI khi dữ liệu thay đổi.
     */
    @Query("SELECT * FROM transactions " +
            "WHERE strftime('%m', date/1000, 'unixepoch') = :month " +
            "AND strftime('%Y', date/1000, 'unixepoch') = :year " +
            "AND user_id = :userId " +
            "ORDER BY date DESC")
    LiveData<List<TransactionWithCategory>> getByMonth(String month, String year, int userId);

    // Tìm kiếm giao dịch cụ thể theo ID
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    LiveData<Transaction> getById(int id);

    // Tính tổng số tiền thu nhập trong một tháng cụ thể
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
            "WHERE type = 'INCOME' " +
            "AND strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear " +
            "AND user_id = :userId")
    LiveData<Double> getTotalIncome(String monthYear, int userId);

    // Tính tổng số tiền đã chi tiêu trong một tháng cụ thể
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
            "WHERE type = 'EXPENSE' " +
            "AND strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear " +
            "AND user_id = :userId")
    LiveData<Double> getTotalExpense(String monthYear, int userId);

    /**
     * Tính số dư (Balance) trong tháng.
     * Công thức: Tổng Thu nhập - Tổng Chi tiêu.
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) " +
            "FROM transactions " +
            "WHERE strftime('%m-%Y', date/1000, 'unixepoch') = :monthYear " +
            "AND user_id = :userId")
    LiveData<Double> getBalance(String monthYear, int userId);

    /**
     * Thống kê tổng chi tiêu theo từng danh mục.
     * Dùng để hiển thị dữ liệu lên biểu đồ tròn (PieChart).
     */
    @Query("SELECT c.name AS categoryName, SUM(t.amount) AS total " +
            "FROM transactions t " +
            "INNER JOIN categories c ON t.category_id = c.id " +
            "WHERE t.type = 'EXPENSE' " +
            "AND t.user_id = :userId " +
            "AND strftime('%m-%Y', t.date/1000, 'unixepoch') = :monthYear " +
            "GROUP BY c.id")
    LiveData<List<CategoryExpense>> getExpenseByCategory(String monthYear, int userId);

    // Lấy toàn bộ giao dịch từ trước đến nay (hỗ trợ cho chức năng tìm kiếm toàn cục)
    @Query("SELECT t.* FROM transactions t " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = :userId " +
            "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> getAllTransactions(int userId);

    @Query("SELECT t.* FROM transactions t " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = :userId " +
            "ORDER BY t.date DESC " +
            "LIMIT :limit OFFSET :offset")
    List<TransactionWithCategory> getTransactionsPaged(int userId, int limit, int offset);

    // Tìm kiếm giao dịch theo từ khóa trong ghi chú hoặc tên danh mục
    @Query("SELECT t.* FROM transactions t " +
            "LEFT JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = :userId AND (t.note LIKE '%' || :query || '%' " +
            "OR c.name LIKE '%' || :query || '%') " +
            "ORDER BY t.date DESC")
    LiveData<List<TransactionWithCategory>> searchTransactions(String query, int userId);
}

package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.Budget;

import java.util.List;

/**
 * Interface DAO để thao tác với bảng 'budgets'.
 */
@Dao
public interface BudgetDao {

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    // Lấy hạn mức chi tiêu tổng của một tháng
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category_id IS NULL AND user_id = :userId LIMIT 1")
    LiveData<Budget> getTotalBudget(int month, int year, int userId);

    // Lấy hạn mức chi tiêu của một danh mục cụ thể trong tháng
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category_id = :categoryId AND user_id = :userId LIMIT 1")
    LiveData<Budget> getCategoryBudget(int month, int year, int categoryId, int userId);

    // Lấy danh sách toàn bộ ngân sách đã thiết lập trong tháng
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND user_id = :userId")
    LiveData<List<Budget>> getAllBudgetsByMonth(int month, int year, int userId);
}

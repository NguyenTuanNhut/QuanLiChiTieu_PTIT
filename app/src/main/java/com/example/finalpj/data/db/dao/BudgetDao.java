package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.Budget;

import java.util.List;

@Dao
public interface BudgetDao {

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category_id IS NULL LIMIT 1")
    LiveData<Budget> getTotalBudget(int month, int year);

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category_id = :categoryId LIMIT 1")
    LiveData<Budget> getCategoryBudget(int month, int year, int categoryId);

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    LiveData<List<Budget>> getAllBudgetsByMonth(int month, int year);
}

package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("SELECT * FROM goals WHERE user_id = :userId ORDER BY deadline ASC")
    LiveData<List<Goal>> getAllGoals(int userId);

    @Query("SELECT * FROM goals WHERE id = :id AND user_id = :userId LIMIT 1")
    LiveData<Goal> getById(int id, int userId);
}

package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalpj.data.db.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Query("SELECT * FROM categories WHERE type = :type")
    LiveData<List<Category>> getByType(String type);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM categories")
    List<Category> getAllSync(); // dùng khi cần sync (trong thread)
}

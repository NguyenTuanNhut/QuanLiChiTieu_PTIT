package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalpj.data.db.entity.Category;

import java.util.List;

/**
 * Interface DAO để thao tác với bảng 'categories'.
 */
@Dao
public interface CategoryDao {

    // Thêm danh mục mới
    @Insert
    void insert(Category category);

    // Lấy danh sách danh mục theo loại (INCOME/EXPENSE)
    @Query("SELECT * FROM categories WHERE type = :type")
    LiveData<List<Category>> getByType(String type);

    // Lấy tất cả danh mục, sắp xếp theo tên A-Z
    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAll();

    // Lấy tất cả danh mục (chạy đồng bộ - Synchronous)
    @Query("SELECT * FROM categories")
    List<Category> getAllSync();

    @Query("SELECT id FROM categories WHERE name = :name LIMIT 1")
    int getCategoryIdByName(String name);
}

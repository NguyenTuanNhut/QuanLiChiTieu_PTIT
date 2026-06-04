package com.example.finalpj.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.CategoryDao;
import com.example.finalpj.data.db.entity.Category;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    public void insert(Category category) {
        executor.execute(() -> categoryDao.insert(category));
    }

    public LiveData<List<Category>> getByType(String type) {
        return categoryDao.getByType(type);
    }

    public LiveData<List<Category>> getAll() {
        return categoryDao.getAll();
    }
}

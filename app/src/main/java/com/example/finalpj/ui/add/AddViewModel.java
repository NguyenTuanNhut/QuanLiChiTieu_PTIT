package com.example.finalpj.ui.add;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.repository.CategoryRepository;
import com.example.finalpj.data.repository.TransactionRepository;

import java.util.List;

public class AddViewModel extends AndroidViewModel {
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private MutableLiveData<String> categoryType = new MutableLiveData<>();
    public LiveData<List<Category>> categories = Transformations.switchMap(categoryType,
            type -> categoryRepository.getByType(type));

    public AddViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    public void update(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    public LiveData<Transaction> getTransactionById(int id) {
        return transactionRepository.getById(id);
    }

    public void loadCategories(String type) {
        categoryType.setValue(type);
    }
}

package com.example.finalpj.ui.history;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;
import com.example.finalpj.data.repository.AuthRepository;
import com.example.finalpj.data.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private int currentUserId;

    private MutableLiveData<List<TransactionWithCategory>> pagedTransactions = new MutableLiveData<>(new ArrayList<>());
    private int currentPage = 0;
    private static final int PAGE_SIZE = 30;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private String filterType = "ALL"; // ALL, INCOME, EXPENSE

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        AuthRepository authRepository = new AuthRepository(application);
        currentUserId = authRepository.getCurrentUserId();
    }

    public LiveData<List<TransactionWithCategory>> getTransactions() {
        if (pagedTransactions.getValue() != null && pagedTransactions.getValue().isEmpty()) {
            loadNextPage();
        }
        return pagedTransactions;
    }

    public void setFilter(String type) {
        if (!this.filterType.equals(type)) {
            this.filterType = type;
            refresh();
        }
    }

    public void loadNextPage() {
        if (isLoading || isLastPage) return;

        isLoading = true;
        new Thread(() -> {
            List<TransactionWithCategory> allData = repository.getPaged(currentUserId, PAGE_SIZE, currentPage * PAGE_SIZE);
            List<TransactionWithCategory> filteredData = new ArrayList<>();
            
            if (allData == null || allData.isEmpty()) {
                isLastPage = true;
            } else {
                for (TransactionWithCategory item : allData) {
                    if (filterType.equals("ALL") || item.transaction.type.equals(filterType)) {
                        filteredData.add(item);
                    }
                }
                
                List<TransactionWithCategory> currentList = pagedTransactions.getValue();
                List<TransactionWithCategory> updatedList = new ArrayList<>(currentList != null ? currentList : new ArrayList<>());
                updatedList.addAll(filteredData);
                pagedTransactions.postValue(updatedList);
                currentPage++;
            }
            isLoading = false;
        }).start();
    }

    public void refresh() {
        currentPage = 0;
        isLastPage = false;
        pagedTransactions.setValue(new ArrayList<>());
        loadNextPage();
    }

    public void deleteTransaction(Transaction transaction) {
        repository.delete(transaction);
        refresh();
    }
}

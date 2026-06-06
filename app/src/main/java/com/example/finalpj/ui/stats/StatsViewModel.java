package com.example.finalpj.ui.stats;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.finalpj.data.db.entity.CategoryExpense;
import com.example.finalpj.data.repository.AuthRepository;
import com.example.finalpj.data.repository.TransactionRepository;

import java.util.Calendar;
import java.util.List;

public class StatsViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private AuthRepository authRepository;
    private int currentUserId;
    private MutableLiveData<String> currentMonthYear = new MutableLiveData<>();

    public StatsViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        authRepository = new AuthRepository(application);
        currentUserId = authRepository.getCurrentUserId();

        Calendar cal = Calendar.getInstance();
        String monthYear = String.format("%02d-%d",
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        currentMonthYear.setValue(monthYear);
    }

    public LiveData<List<CategoryExpense>> getExpenseByCategory() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getExpenseByCategory(monthYear, currentUserId));
    }

    public LiveData<Double> getTotalIncome() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalIncome(monthYear, currentUserId));
    }

    public LiveData<Double> getTotalExpense() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalExpense(monthYear, currentUserId));
    }

    public void setMonth(int month, int year) {
        currentMonthYear.setValue(String.format("%02d-%d", month, year));
    }

    public String getCurrentMonthYear() {
        return currentMonthYear.getValue();
    }
}

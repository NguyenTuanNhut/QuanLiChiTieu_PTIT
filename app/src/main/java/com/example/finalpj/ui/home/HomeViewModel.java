package com.example.finalpj.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import android.os.Handler;
import android.os.Looper;

import com.example.finalpj.data.db.entity.Budget;
import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.CategoryExpense;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;
import com.example.finalpj.data.repository.BudgetRepository;
import com.example.finalpj.data.repository.CategoryRepository;
import com.example.finalpj.data.repository.TransactionRepository;
import com.example.finalpj.ui.adapter.BudgetAdapter;
import com.example.finalpj.utils.SearchUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private MutableLiveData<String> currentMonthYear = new MutableLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        budgetRepository = new BudgetRepository(application);
        categoryRepository = new CategoryRepository(application);
        // Set tháng hiện tại
        Calendar cal = Calendar.getInstance();
        String monthYear = String.format("%02d-%d",
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        currentMonthYear.setValue(monthYear);
    }

    public LiveData<Double> getTotalIncome() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalIncome(monthYear));
    }

    public LiveData<Double> getTotalExpense() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalExpense(monthYear));
    }

    public LiveData<Double> getBalance() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getBalance(monthYear));
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return Transformations.switchMap(currentMonthYear, monthYear -> {
            String[] parts = monthYear.split("-");
            if (parts.length != 2) {
                return new MutableLiveData<>(new ArrayList<>());
            }
            return repository.getByMonth(parts[0], parts[1]);
        });
    }

    public LiveData<List<TransactionWithCategory>> getSearchResults() {
        MediatorLiveData<List<TransactionWithCategory>> result = new MediatorLiveData<>();
        LiveData<List<TransactionWithCategory>> allSource = repository.getAll();
        
        result.addSource(searchQuery, query -> {
            filterTransactions(result, allSource.getValue(), query);
        });
        
        result.addSource(allSource, transactions -> {
            filterTransactions(result, transactions, searchQuery.getValue());
        });
        
        return result;
    }

    private void filterTransactions(MediatorLiveData<List<TransactionWithCategory>> result, 
                                    List<TransactionWithCategory> all, 
                                    String query) {
        if (all == null) return;
        
        List<TransactionWithCategory> filtered = new ArrayList<>();
        boolean isEmptyQuery = (query == null || query.isEmpty());
        
        // Lấy tháng/năm hiện tại để lọc nếu không có query
        String[] parts = currentMonthYear.getValue() != null ? currentMonthYear.getValue().split("-") : new String[0];
        String currentMonth = parts.length > 0 ? parts[0] : "";
        String currentYear = parts.length > 1 ? parts[1] : "";

        for (TransactionWithCategory item : all) {
            if (isEmptyQuery) {
                // Nếu không search, chỉ hiện các giao dịch của tháng đang chọn
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(item.transaction.date);
                String itemMonth = String.format("%02d", cal.get(Calendar.MONTH) + 1);
                String itemYear = String.valueOf(cal.get(Calendar.YEAR));
                
                if (itemMonth.equals(currentMonth) && itemYear.equals(currentYear)) {
                    filtered.add(item);
                }
            } else {
                // Nếu có search, tìm kiếm trên toàn bộ dữ liệu
                String categoryName = item.category != null ? item.category.name : "";
                String note = item.transaction.note != null ? item.transaction.note : "";
                
                if (SearchUtils.matches(categoryName, query) || SearchUtils.matches(note, query)) {
                    filtered.add(item);
                }
            }
        }
        result.setValue(filtered);
    }

    public void setSearchQuery(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        searchRunnable = () -> searchQuery.setValue(query);
        searchHandler.postDelayed(searchRunnable, 400); // Đợi 400ms sau khi ngừng gõ mới tìm
    }

    public LiveData<List<Budget>> getBudgets() {
        return Transformations.switchMap(currentMonthYear, monthYear -> {
            String[] parts = monthYear.split("-");
            if (parts.length != 2) return new MutableLiveData<>(new ArrayList<>());
            return budgetRepository.getAllBudgetsByMonth(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        });
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAll();
    }

    public LiveData<List<com.example.finalpj.data.db.entity.CategoryExpense>> getExpenseByCategory() {
        return Transformations.switchMap(currentMonthYear, monthYear -> repository.getExpenseByCategory(monthYear));
    }

    public LiveData<List<BudgetAdapter.BudgetWithProgress>> getBudgetStatus() {
        MediatorLiveData<List<BudgetAdapter.BudgetWithProgress>> result = new MediatorLiveData<>();

        LiveData<List<Budget>> budgetsSource = getBudgets();
        LiveData<List<Category>> categoriesSource = getAllCategories();
        LiveData<List<CategoryExpense>> expensesSource = getExpenseByCategory();

        result.addSource(budgetsSource, budgets -> updateBudgetStatus(result, budgets, categoriesSource.getValue(), expensesSource.getValue()));
        result.addSource(categoriesSource, categories -> updateBudgetStatus(result, budgetsSource.getValue(), categories, expensesSource.getValue()));
        result.addSource(expensesSource, expenses -> updateBudgetStatus(result, budgetsSource.getValue(), categoriesSource.getValue(), expenses));

        return result;
    }

    private void updateBudgetStatus(MediatorLiveData<List<BudgetAdapter.BudgetWithProgress>> result,
                                    List<Budget> budgets,
                                    List<Category> categories,
                                    List<CategoryExpense> expenses) {
        if (budgets == null) return;

        List<BudgetAdapter.BudgetWithProgress> statusList = new ArrayList<>();
        Map<Integer, Category> categoryMap = new HashMap<>();
        if (categories != null) {
            for (Category c : categories) categoryMap.put(c.id, c);
        }

        Map<String, Double> expenseMap = new HashMap<>();
        if (expenses != null) {
            for (CategoryExpense e : expenses) expenseMap.put(e.categoryName, e.total);
        }

        for (Budget b : budgets) {
            BudgetAdapter.BudgetWithProgress status = new BudgetAdapter.BudgetWithProgress();
            status.budget = b;
            if (b.categoryId != null) {
                status.category = categoryMap.get(b.categoryId);
                String catName = status.category != null ? status.category.name : "";
                Double spent = expenseMap.get(catName);
                status.spent = spent != null ? spent : 0.0;
            } else {
                status.spent = 0;
            }
            statusList.add(status);
        }
        result.setValue(statusList);
    }

    public void setMonth(int month, int year) {
        currentMonthYear.setValue(String.format("%02d-%d", month, year));
    }

    public String getCurrentMonthYear() {
        return currentMonthYear.getValue();
    }
}

package com.example.finalpj.ui.home;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.finalpj.data.db.entity.Budget;
import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.CategoryExpense;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;
import com.example.finalpj.data.repository.AuthRepository;
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

/**
 * ViewModel quản lý dữ liệu cho màn hình Trang chủ (Home).
 * Sử dụng LiveData để kết nối dữ liệu từ Repository đến Fragment.
 */
public class HomeViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private AuthRepository authRepository;
    private int currentUserId;
    
    // Lưu giữ tháng/năm đang được chọn để xem báo cáo
    private MutableLiveData<String> currentMonthYear = new MutableLiveData<>();
    // Lưu giữ từ khóa tìm kiếm của người dùng
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    
    // Cơ chế Debounce để trì hoãn việc tìm kiếm khi người dùng đang gõ phím
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        budgetRepository = new BudgetRepository(application);
        categoryRepository = new CategoryRepository(application);
        authRepository = new AuthRepository(application);
        currentUserId = authRepository.getCurrentUserId();
        
        // Mặc định thiết lập tháng xem báo cáo là tháng hiện tại
        Calendar cal = Calendar.getInstance();
        String monthYear = String.format("%02d-%d",
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        currentMonthYear.setValue(monthYear);

        // Tự động làm mới danh sách phân trang khi database thay đổi (thêm/xóa giao dịch)
        pagedTransactions.addSource(repository.getCount(currentUserId), count -> {
            refreshTransactions();
        });
    }

    // Lấy LiveData tổng thu nhập dựa trên tháng đang chọn
    public LiveData<Double> getTotalIncome() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalIncome(monthYear, currentUserId));
    }

    // Lấy LiveData tổng chi tiêu dựa trên tháng đang chọn
    public LiveData<Double> getTotalExpense() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getTotalExpense(monthYear, currentUserId));
    }

    // Lấy LiveData số dư dựa trên tháng đang chọn
    public LiveData<Double> getBalance() {
        return Transformations.switchMap(currentMonthYear,
                monthYear -> repository.getBalance(monthYear, currentUserId));
    }

    // Lấy danh sách giao dịch của tháng đang chọn
    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return Transformations.switchMap(currentMonthYear, monthYear -> {
            String[] parts = monthYear.split("-");
            if (parts.length != 2) {
                return new MutableLiveData<>(new ArrayList<>());
            }
            return repository.getByMonth(parts[0], parts[1], currentUserId);
        });
    }

    /**
     * Chức năng tìm kiếm thông minh.
     * Kết hợp nhiều nguồn dữ liệu (Toàn bộ giao dịch và Từ khóa search) để lọc kết quả.
     */
    public LiveData<List<TransactionWithCategory>> getSearchResults() {
        MediatorLiveData<List<TransactionWithCategory>> result = new MediatorLiveData<>();
        LiveData<List<TransactionWithCategory>> allSource = repository.getAll(currentUserId);
        
        result.addSource(searchQuery, query -> {
            filterTransactions(result, allSource.getValue(), query);
        });
        
        result.addSource(allSource, transactions -> {
            filterTransactions(result, transactions, searchQuery.getValue());
        });
        
        return result;
    }

    /**
     * Logic lọc giao dịch dựa trên từ khóa (bỏ qua dấu tiếng Việt).
     */
    private void filterTransactions(MediatorLiveData<List<TransactionWithCategory>> result, 
                                    List<TransactionWithCategory> all, 
                                    String query) {
        if (all == null) return;
        
        List<TransactionWithCategory> filtered = new ArrayList<>();
        boolean isEmptyQuery = (query == null || query.isEmpty());
        
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
                // Nếu có search, tìm kiếm không dấu trên toàn bộ lịch sử
                String categoryName = item.category != null ? item.category.name : "";
                String note = item.transaction.note != null ? item.transaction.note : "";
                
                if (SearchUtils.matches(categoryName, query) || SearchUtils.matches(note, query)) {
                    filtered.add(item);
                }
            }
        }
        result.setValue(filtered);
    }

    /**
     * Thiết lập từ khóa tìm kiếm kèm theo cơ chế Debounce để sửa lỗi gõ tiếng Việt.
     */
    public void setSearchQuery(String query) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        searchRunnable = () -> searchQuery.setValue(query);
        searchHandler.postDelayed(searchRunnable, 400); 
    }

    // Lấy dữ liệu ngân sách (Budget)
    public LiveData<List<Budget>> getBudgets() {
        return Transformations.switchMap(currentMonthYear, monthYear -> {
            String[] parts = monthYear.split("-");
            if (parts.length != 2) return new MutableLiveData<>(new ArrayList<>());
            return budgetRepository.getAllBudgetsByMonth(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), currentUserId);
        });
    }

    // Lấy danh sách tất cả danh mục
    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAll();
    }

    // Lấy chi tiêu theo danh mục (cho thống kê)
    public LiveData<List<com.example.finalpj.data.db.entity.CategoryExpense>> getExpenseByCategory() {
        return Transformations.switchMap(currentMonthYear, monthYear -> repository.getExpenseByCategory(monthYear, currentUserId));
    }

    /**
     * Kết hợp dữ liệu Ngân sách, Danh mục và Chi tiêu thực tế 
     * để hiển thị thanh tiến độ (ProgressBar) ngân sách.
     */
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

    // Thay đổi tháng xem báo cáo
    public void setMonth(int month, int year) {
        currentMonthYear.setValue(String.format("%02d-%d", month, year));
    }

    public String getCurrentMonthYear() {
        return currentMonthYear.getValue();
    }

    // Xóa một giao dịch
    public void deleteTransaction(Transaction transaction) {
        repository.delete(transaction);
    }

    // --- Infinite Scroll Logic ---
    private MediatorLiveData<List<TransactionWithCategory>> pagedTransactions = new MediatorLiveData<>();
    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    public LiveData<List<TransactionWithCategory>> getPagedTransactions() {
        if (pagedTransactions.getValue() == null) {
            loadNextPage();
        }
        return pagedTransactions;
    }

    public void loadNextPage() {
        if (isLoading || isLastPage) return;

        isLoading = true;
        new Thread(() -> {
            List<TransactionWithCategory> newData = repository.getPaged(currentUserId, PAGE_SIZE, currentPage * PAGE_SIZE);
            if (newData == null || newData.isEmpty()) {
                isLastPage = true;
            } else {
                List<TransactionWithCategory> currentList = pagedTransactions.getValue();
                List<TransactionWithCategory> updatedList = new ArrayList<>(currentList != null ? currentList : new ArrayList<>());
                updatedList.addAll(newData);
                pagedTransactions.postValue(updatedList);
                currentPage++;
            }
            isLoading = false;
        }).start();
    }

    public void refreshTransactions() {
        currentPage = 0;
        isLastPage = false;
        pagedTransactions.setValue(new ArrayList<>());
        loadNextPage();
    }
}

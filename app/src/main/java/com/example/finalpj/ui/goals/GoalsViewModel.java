package com.example.finalpj.ui.goals;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.finalpj.data.db.entity.Goal;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.repository.AuthRepository;
import com.example.finalpj.data.repository.CategoryRepository;
import com.example.finalpj.data.repository.GoalRepository;
import com.example.finalpj.data.repository.TransactionRepository;
import java.util.List;

public class GoalsViewModel extends AndroidViewModel {
    private GoalRepository repository;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private AuthRepository authRepository;
    private int currentUserId;

    public GoalsViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalRepository(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        authRepository = new AuthRepository(application);
        currentUserId = authRepository.getCurrentUserId();
    }

    public LiveData<List<Goal>> getGoals() {
        return repository.getAllGoals(currentUserId);
    }

    public void insert(Goal goal) {
        goal.userId = currentUserId;
        repository.insert(goal);
    }

    public void update(Goal goal) {
        repository.update(goal);
    }

    public void delete(Goal goal) {
        repository.delete(goal);
    }

    public void addMoney(Goal goal, double amount) {
        new Thread(() -> {
            // 1. Cập nhật số tiền hiện tại của mục tiêu
            goal.currentAmount += amount;
            repository.update(goal);

            // 2. Lấy ID danh mục "Tiết kiệm"
            int catId = categoryRepository.getCategoryIdByName("Tiết kiệm");
            if (catId == 0) catId = 8; // Fallback về "Khác" nếu không tìm thấy

            // 3. Tạo một giao dịch chi tiêu để trừ vào số dư và hiện trong lịch sử
            Transaction transaction = new Transaction();
            transaction.amount = amount;
            transaction.type = "EXPENSE";
            transaction.note = "Nạp tiền mục tiêu: " + goal.name;
            transaction.date = System.currentTimeMillis();
            transaction.userId = currentUserId;
            transaction.categoryId = catId;
            transaction.createdAt = System.currentTimeMillis();

            transactionRepository.insert(transaction);
        }).start();
    }
}

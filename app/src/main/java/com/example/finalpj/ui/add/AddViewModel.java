package com.example.finalpj.ui.add;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.repository.AuthRepository;
import com.example.finalpj.data.repository.CategoryRepository;
import com.example.finalpj.data.repository.TransactionRepository;

import java.util.List;

/**
 * ViewModel xử lý logic nghiệp vụ cho màn hình Thêm/Sửa giao dịch.
 * Đóng vai trò trung gian giữa UI và Repository.
 */
public class AddViewModel extends AndroidViewModel {
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private AuthRepository authRepository;
    private int currentUserId;

    // LiveData lưu giữ loại danh mục đang chọn (INCOME/EXPENSE) để lọc danh sách icon
    private MutableLiveData<String> categoryType = new MutableLiveData<>();
    
    // Tự động chuyển đổi danh sách danh mục khi categoryType thay đổi
    public LiveData<List<Category>> categories = Transformations.switchMap(categoryType,
            type -> categoryRepository.getByType(type));

    public AddViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        authRepository = new AuthRepository(application);
        currentUserId = authRepository.getCurrentUserId();
    }

    /**
     * Gửi yêu cầu thêm giao dịch xuống Repository.
     */
    public void insert(Transaction transaction) {
        transaction.userId = currentUserId;
        transactionRepository.insert(transaction);
    }

    /**
     * Gửi yêu cầu cập nhật giao dịch hiện có.
     */
    public void update(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    /**
     * Gửi yêu cầu xóa giao dịch.
     */
    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    /**
     * Lấy thông tin chi tiết của một giao dịch dựa trên ID.
     */
    public LiveData<Transaction> getTransactionById(int id) {
        return transactionRepository.getById(id);
    }

    /**
     * Thiết lập loại danh mục cần nạp (Thu nhập hay Chi tiêu).
     */
    public void loadCategories(String type) {
        categoryType.setValue(type);
    }

    public void insertCategory(Category category) {
        categoryRepository.insert(category);
    }
}

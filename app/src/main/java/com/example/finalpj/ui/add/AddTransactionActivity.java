package com.example.finalpj.ui.add;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.ui.adapter.CategoryAdapter;
import com.example.finalpj.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

/**
 * Activity xử lý việc Thêm mới hoặc Chỉnh sửa một giao dịch.
 */
public class AddTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_TRANSACTION_ID = "extra_transaction_id";

    private TextInputEditText etAmount, etNote, etDate;
    private MaterialButton btnDelete;
    private AddViewModel viewModel;
    private String selectedType = "EXPENSE"; // Mặc định là Chi tiêu
    private int selectedCategoryId = -1;
    private long selectedDate;
    private CategoryAdapter categoryAdapter;
    private Transaction currentTransaction;
    private boolean isEditMode = false; // Cờ xác định đang Thêm mới hay Chỉnh sửa
    private boolean isProgrammaticTabSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Khởi tạo ViewModel và ánh xạ View
        viewModel = new ViewModelProvider(this).get(AddViewModel.class);
        etAmount = findViewById(R.id.et_amount);
        etNote = findViewById(R.id.et_note);
        etDate = findViewById(R.id.et_date);
        btnDelete = findViewById(R.id.btn_delete);

        // Mặc định chọn ngày hiện tại
        selectedDate = System.currentTimeMillis();
        etDate.setText(DateUtils.formatDate(selectedDate));

        // Mở DatePicker khi click vào ô nhập ngày
        etDate.setOnClickListener(v -> showDatePicker());

        // Cấu hình TabLayout để chuyển đổi giữa Thu nhập và Chi tiêu
        TabLayout tabType = findViewById(R.id.tab_type);
        tabType.addTab(tabType.newTab().setText("Chi tiêu"));
        tabType.addTab(tabType.newTab().setText("Thu nhập"));
        tabType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedType = tab.getPosition() == 0 ? "EXPENSE" : "INCOME";
                // Load danh sách danh mục tương ứng với loại đang chọn
                viewModel.loadCategories(selectedType);
                if (!isProgrammaticTabSelect) {
                    selectedCategoryId = -1; // Reset danh mục khi đổi loại
                }
                isProgrammaticTabSelect = false;
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Xử lý sự kiện nút Lưu và nút Xóa
        findViewById(R.id.btn_save).setOnClickListener(v -> saveTransaction());
        btnDelete.setOnClickListener(v -> deleteTransaction());
        btnDelete.setVisibility(View.GONE); // Mặc định ẩn nút Xóa nếu là thêm mới

        // Cấu hình danh sách Danh mục dưới dạng lưới (Grid)
        RecyclerView rvCategories = findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 4));
        categoryAdapter = new CategoryAdapter();
        rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(category -> {
            selectedCategoryId = category.id;
        });

        // Quan sát danh sách danh mục từ ViewModel
        viewModel.categories.observe(this, categories -> {
            if (categories != null) {
                categoryAdapter.setCategories(categories);
                if (selectedCategoryId != -1) {
                    categoryAdapter.setSelectedCategoryId(selectedCategoryId);
                }
            }
        });

        // Kiểm tra xem có nhận được ID giao dịch không (để vào chế độ Chỉnh sửa)
        int transactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        if (transactionId != -1) {
            isEditMode = true;
            btnDelete.setVisibility(View.VISIBLE); // Hiện nút Xóa trong chế độ chỉnh sửa
            viewModel.getTransactionById(transactionId).observe(this, transaction -> {
                if (transaction != null) {
                    currentTransaction = transaction;
                    populateTransaction(transaction, tabType);
                }
            });
        } else {
            viewModel.loadCategories("EXPENSE");
        }
    }

    /**
     * Điền dữ liệu cũ vào các ô nhập liệu khi ở chế độ Chỉnh sửa.
     */
    private void populateTransaction(Transaction transaction, TabLayout tabType) {
        selectedCategoryId = transaction.categoryId;
        selectedType = transaction.type;
        selectedDate = transaction.date;
        etAmount.setText(String.valueOf(transaction.amount));
        etNote.setText(transaction.note);
        etDate.setText(DateUtils.formatDate(transaction.date));

        int tabIndex = "EXPENSE".equals(transaction.type) ? 0 : 1;
        TabLayout.Tab tab = tabType.getTabAt(tabIndex);
        if (tab != null) {
            isProgrammaticTabSelect = true;
            tab.select();
        }
    }

    /**
     * Thực hiện lưu giao dịch (Insert hoặc Update) vào Database.
     */
    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu (Validation)
        if (amountStr.isEmpty()) {
            etAmount.setError("Vui lòng nhập số tiền");
            return;
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ");
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Số tiền phải lớn hơn 0");
            return;
        }

        Transaction t;
        if (isEditMode && currentTransaction != null) {
            t = currentTransaction;
        } else {
            t = new Transaction();
            t.createdAt = System.currentTimeMillis();
        }

        t.amount = amount;
        t.type = selectedType;
        t.categoryId = selectedCategoryId;
        t.note = etNote.getText().toString().trim();
        t.date = selectedDate;

        // Gọi ViewModel để thực hiện lưu
        if (isEditMode) {
            viewModel.update(t);
            Toast.makeText(this, "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(t);
            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
        }
        finish(); // Đóng màn hình sau khi lưu xong
    }

    /**
     * Xử lý xóa giao dịch.
     */
    private void deleteTransaction() {
        if (currentTransaction != null) {
            viewModel.delete(currentTransaction);
            Toast.makeText(this, "Đã xóa giao dịch", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Hiển thị hộp thoại chọn ngày.
     */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    cal.set(year, month, day);
                    selectedDate = cal.getTimeInMillis();
                    etDate.setText(DateUtils.formatDate(selectedDate));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}

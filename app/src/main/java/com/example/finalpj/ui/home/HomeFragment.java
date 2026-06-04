package com.example.finalpj.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.ui.add.AddTransactionActivity;
import java.util.Calendar;

import com.example.finalpj.R;
import com.example.finalpj.ui.adapter.BudgetAdapter;
import com.example.finalpj.ui.adapter.TransactionAdapter;
import com.example.finalpj.utils.CurrencyUtils;

/**
 * Lớp điều khiển chính cho màn hình Trang chủ.
 * Chịu trách nhiệm hiển thị Tổng quan tài chính, Ngân sách và Giao dịch gần đây.
 */
public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private TextView tvBalance, tvIncome, tvExpense, tvMonthLabel;
    private TransactionAdapter adapter;
    private BudgetAdapter budgetAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Nạp giao dịch từ file layout XML
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ các thành phần giao diện
        tvBalance = view.findViewById(R.id.tv_balance);
        tvIncome = view.findViewById(R.id.tv_income);
        tvExpense = view.findViewById(R.id.tv_expense);
        tvMonthLabel = view.findViewById(R.id.tv_month_label);

        // Cấu hình ô Tìm kiếm (SearchView)
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Cập nhật từ khóa tìm kiếm vào ViewModel mỗi khi nội dung thay đổi
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

        // Thiết lập danh sách Giao dịch gần đây sử dụng RecyclerView
        RecyclerView rvRecent = view.findViewById(R.id.rv_recent);
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        rvRecent.setAdapter(adapter);

        // Thiết lập danh sách Ngân sách (Budget) sử dụng RecyclerView
        RecyclerView rvBudgets = view.findViewById(R.id.rv_budgets);
        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        budgetAdapter = new BudgetAdapter();
        rvBudgets.setAdapter(budgetAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Theo dõi (Observe) sự thay đổi của Tổng thu nhập để cập nhật lên UI
        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            tvIncome.setText(CurrencyUtils.format(income != null ? income : 0));
        });

        // Theo dõi tổng chi tiêu
        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            tvExpense.setText(CurrencyUtils.format(expense != null ? expense : 0));
        });

        // Theo dõi số dư và thay đổi màu sắc (Trắng nếu dương, Đỏ nếu âm)
        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            tvBalance.setText(CurrencyUtils.format(balance != null ? balance : 0));
            tvBalance.setTextColor((balance != null && balance >= 0) ? Color.WHITE : Color.parseColor("#FF8A80"));
        });

        // Hiển thị tháng đang xem và cho phép người dùng chọn tháng khác
        tvMonthLabel.setText(formatMonthLabel(viewModel.getCurrentMonthYear()));
        tvMonthLabel.setOnClickListener(v -> showMonthPicker());

        // Xử lý sự kiện khi người dùng nhấn vào một giao dịch để Sửa/Xóa
        adapter.setOnTransactionClickListener(transaction -> {
            Intent intent = new Intent(getContext(), com.example.finalpj.ui.add.AddTransactionActivity.class);
            intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, transaction.id);
            startActivity(intent);
        });

        // Theo dõi danh sách giao dịch (bao gồm cả kết quả tìm kiếm)
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
            }
        });

        // Theo dõi tình trạng ngân sách (đã tiêu bao nhiêu %)
        viewModel.getBudgetStatus().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                budgetAdapter.setItems(budgets);
            }
        });
    }

    /**
     * Hiển thị hộp thoại chọn tháng (Month Picker).
     */
    private void showMonthPicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            viewModel.setMonth(month + 1, year);
            tvMonthLabel.setText(formatMonthLabel(String.format("%02d-%d", month + 1, year)));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     * Định dạng chuỗi hiển thị tháng (vd: 01-2025 -> Tháng 1/2025).
     */
    private String formatMonthLabel(String monthYear) {
        if (monthYear == null || monthYear.isEmpty()) {
            return "Tháng";
        }
        String[] parts = monthYear.split("-");
        if (parts.length != 2)
            return monthYear;
        return "Tháng " + Integer.parseInt(parts[0]) + "/" + parts[1];
    }
}

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

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private TextView tvBalance, tvIncome, tvExpense, tvMonthLabel;
    private TransactionAdapter adapter;
    private BudgetAdapter budgetAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvBalance = view.findViewById(R.id.tv_balance);
        tvIncome = view.findViewById(R.id.tv_income);
        tvExpense = view.findViewById(R.id.tv_expense);
        tvMonthLabel = view.findViewById(R.id.tv_month_label);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Chỉ gửi dữ liệu đi, không đóng bàn phím hay can thiệp vào UI lúc này
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

        RecyclerView rvRecent = view.findViewById(R.id.rv_recent);
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        rvRecent.setAdapter(adapter);

        RecyclerView rvBudgets = view.findViewById(R.id.rv_budgets);
        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        budgetAdapter = new BudgetAdapter();
        rvBudgets.setAdapter(budgetAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe thu nhập
        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            tvIncome.setText(CurrencyUtils.format(income != null ? income : 0));
        });

        // Observe chi tiêu
        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            tvExpense.setText(CurrencyUtils.format(expense != null ? expense : 0));
        });

        // Observe balance derived directly from DB
        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            tvBalance.setText(CurrencyUtils.format(balance != null ? balance : 0));
            tvBalance.setTextColor((balance != null && balance >= 0) ? Color.WHITE : Color.parseColor("#FF8A80"));
        });

        tvMonthLabel.setText(formatMonthLabel(viewModel.getCurrentMonthYear()));
        tvMonthLabel.setOnClickListener(v -> showMonthPicker());

        // Observe danh sách giao dịch
        adapter.setOnTransactionClickListener(transaction -> {
            Intent intent = new Intent(getContext(), com.example.finalpj.ui.add.AddTransactionActivity.class);
            intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, transaction.id);
            startActivity(intent);
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
            }
        });

        viewModel.getBudgetStatus().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                budgetAdapter.setItems(budgets);
            }
        });
    }

    private void showMonthPicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            viewModel.setMonth(month + 1, year);
            tvMonthLabel.setText(formatMonthLabel(String.format("%02d-%d", month + 1, year)));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String formatMonthLabel(String monthYear) {
        if (monthYear == null || monthYear.isEmpty()) {
            return "Tháng";
        }
        String[] parts = monthYear.split("-");
        if (parts.length != 2)
            return monthYear;
        return "Tháng " + Integer.parseInt(parts[0]) + "/" + parts[1];
    }

    private void updateBalance() {
        Double income = viewModel.getTotalIncome().getValue();
        Double expense = viewModel.getTotalExpense().getValue();

        double incomeVal = income != null ? income : 0;
        double expenseVal = expense != null ? expense : 0;

        double balance = incomeVal - expenseVal;
        tvBalance.setText(CurrencyUtils.format(balance));
        tvBalance.setTextColor(balance >= 0 ? Color.WHITE : Color.parseColor("#FF8A80"));
    }
}

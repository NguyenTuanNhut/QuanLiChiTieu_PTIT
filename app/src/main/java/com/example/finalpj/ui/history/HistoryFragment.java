package com.example.finalpj.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.TransactionWithCategory;
import com.example.finalpj.ui.adapter.TransactionAdapter;
import com.example.finalpj.ui.add.AddTransactionActivity;
import com.example.finalpj.utils.SwipeToDeleteCallback;
import com.google.android.material.chip.ChipGroup;

public class HistoryFragment extends Fragment {
    private HistoryViewModel viewModel;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        RecyclerView rvHistory = view.findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter();
        rvHistory.setAdapter(adapter);

        ChipGroup chipGroup = view.findViewById(R.id.chip_group_filter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_all)) viewModel.setFilter("ALL");
            else if (checkedIds.contains(R.id.chip_income)) viewModel.setFilter("INCOME");
            else if (checkedIds.contains(R.id.chip_expense)) viewModel.setFilter("EXPENSE");
        });

        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
            }
        });

        rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadNextPage();
                }
            }
        });

        // Swipe to Edit/Delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TransactionWithCategory item = adapter.getTransactionAt(position);
                
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.deleteTransaction(item.transaction);
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), AddTransactionActivity.class);
                    intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, item.transaction.id);
                    startActivity(intent);
                    adapter.notifyItemChanged(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(rvHistory);

        adapter.setOnTransactionClickListener(transaction -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, transaction.id);
            startActivity(intent);
        });
    }
}

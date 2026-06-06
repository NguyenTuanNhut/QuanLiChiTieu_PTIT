package com.example.finalpj.ui.goals;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;
import com.example.finalpj.ui.adapter.GoalAdapter;
import com.example.finalpj.data.db.entity.Goal;

public class GoalsFragment extends Fragment {
    private GoalsViewModel viewModel;
    private GoalAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        RecyclerView rvGoals = view.findViewById(R.id.rv_goals);
        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GoalAdapter();
        rvGoals.setAdapter(adapter);

        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null) {
                adapter.setGoals(goals);
            }
        });

        adapter.setOnGoalClickListener(goal -> {
            showGoalOptionsDialog(goal);
        });
    }

    private void showGoalOptionsDialog(Goal goal) {
        String[] options = {"Nạp tiền vào mục tiêu", "Chỉnh sửa mục tiêu", "Xóa mục tiêu"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(goal.name)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddMoneyDialog(goal);
                    } else if (which == 1) {
                        Intent intent = new Intent(getContext(), AddGoalActivity.class);
                        intent.putExtra(AddGoalActivity.EXTRA_GOAL_ID, goal.id);
                        startActivity(intent);
                    } else if (which == 2) {
                        viewModel.delete(goal);
                        Toast.makeText(getContext(), "Đã xóa mục tiêu", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showAddMoneyDialog(Goal goal) {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Nhập số tiền (₫)");

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Nạp tiền vào: " + goal.name)
                .setView(input)
                .setPositiveButton("Nạp tiền", (dialog, which) -> {
                    String amountStr = input.getText().toString();
                    if (!amountStr.isEmpty()) {
                        double amount = Double.parseDouble(amountStr);
                        viewModel.addMoney(goal, amount);
                        Toast.makeText(getContext(), "Đã cập nhật tiến độ và ghi nhận giao dịch!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

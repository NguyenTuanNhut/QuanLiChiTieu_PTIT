package com.example.finalpj.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.CategoryExpense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private StatsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        PieChart pieChart = view.findViewById(R.id.pie_chart);
        setupPieChart(pieChart);

        viewModel.getExpenseByCategory().observe(getViewLifecycleOwner(), expenses -> {
            loadPieData(pieChart, expenses);
        });
    }

    private void setupPieChart(PieChart chart) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setCenterText("Chi tiêu\ntheo danh mục");
        chart.animateY(1000);
    }

    private void loadPieData(PieChart chart, List<CategoryExpense> expenses) {
        List<PieEntry> entries = new ArrayList<>();
        if (expenses != null && !expenses.isEmpty()) {
            for (CategoryExpense expense : expenses) {
                entries.add(new PieEntry((float) expense.total, expense.categoryName));
            }
        } else {
            entries.add(new PieEntry(1f, "Không có dữ liệu"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#FF5252"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#607D8B"));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();
    }
}

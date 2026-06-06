package com.example.finalpj.ui.stats;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.CategoryExpense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị Thống kê chi tiêu dưới dạng biểu đồ tròn (PieChart).
 */
public class StatsFragment extends Fragment {

    private StatsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Nạp giao diện thống kê
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        // Tìm và cấu hình PieChart từ layout
        PieChart pieChart = view.findViewById(R.id.pie_chart);
        setupPieChart(pieChart);

        // Theo dõi dữ liệu chi tiêu theo danh mục để cập nhật lên biểu đồ
        viewModel.getExpenseByCategory().observe(getViewLifecycleOwner(), expenses -> {
            loadPieData(pieChart, expenses);
        });

        view.findViewById(R.id.btn_export_pdf).setOnClickListener(v -> exportToPDF());
    }

    private void exportToPDF() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setTextSize(16);
        paint.setColor(Color.BLACK);

        page.getCanvas().drawText("BÁO CÁO TÀI CHÍNH - PRUDENTIAL FINANCE", 50, 50, paint);
        paint.setTextSize(12);
        page.getCanvas().drawText("Tháng: " + viewModel.getCurrentMonthYear(), 50, 80, paint);
        
        Double income = viewModel.getTotalIncome().getValue();
        Double expense = viewModel.getTotalExpense().getValue();
        
        page.getCanvas().drawText("Tổng thu nhập: " + (income != null ? income : 0) + " VND", 50, 110, paint);
        page.getCanvas().drawText("Tổng chi tiêu: " + (expense != null ? expense : 0) + " VND", 50, 130, paint);
        page.getCanvas().drawText("Số dư: " + ((income != null ? income : 0) - (expense != null ? expense : 0)) + " VND", 50, 150, paint);

        document.finishPage(page);

        File file = new File(requireContext().getExternalFilesDir(null), "BaoCaoTaiChinh.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "Đã xuất PDF tại: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi xuất PDF", Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    private void setupPieChart(PieChart chart) {
        int textColor = getThemeColor(android.R.attr.textColorPrimary);
        int surfaceColor = getThemeColor(com.google.android.material.R.attr.colorSurface);

        chart.setUsePercentValues(true); // Hiển thị giá trị phần trăm
        chart.getDescription().setEnabled(false); // Ẩn mô tả
        chart.setDrawHoleEnabled(true); // Vẽ lỗ trống ở giữa biểu đồ
        chart.setHoleColor(surfaceColor);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setCenterText("Chi tiêu\ntheo danh mục"); // Chữ hiển thị ở giữa lỗ trống
        chart.setCenterTextColor(textColor);
        chart.getLegend().setTextColor(textColor);
        chart.animateY(1000); // Hiệu ứng xoay khi nạp dữ liệu
    }

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    /**
     * Nạp dữ liệu vào biểu đồ tròn.
     */
    private void loadPieData(PieChart chart, List<CategoryExpense> expenses) {
        List<PieEntry> entries = new ArrayList<>();
        if (expenses != null && !expenses.isEmpty()) {
            for (CategoryExpense expense : expenses) {
                // Thêm một phần (slice) vào biểu đồ
                entries.add(new PieEntry((float) expense.total, expense.categoryName));
            }
        } else {
            // Hiển thị phần trống nếu không có dữ liệu
            entries.add(new PieEntry(1f, "Không có dữ liệu"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        // Thiết lập bảng màu cho các phần khác nhau
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
        chart.invalidate(); // Vẽ lại biểu đồ
    }
}

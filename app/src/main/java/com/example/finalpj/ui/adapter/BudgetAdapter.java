package com.example.finalpj.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.Budget;
import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị tiến độ Ngân sách (Budget).
 * Giúp người dùng theo dõi xem mình đã chi tiêu bao nhiêu % so với hạn mức đề ra.
 */
public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
    private List<BudgetWithProgress> items = new ArrayList<>();

    /**
     * Lớp POJO kết hợp thông tin Ngân sách và số tiền thực tế đã chi tiêu.
     */
    public static class BudgetWithProgress {
        public Budget budget;
        public Category category;
        public double spent;
    }

    public void setItems(List<BudgetWithProgress> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item ngân sách kèm thanh tiến độ
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetWithProgress item = items.get(position);
        // Hiển thị tên danh mục áp dụng ngân sách
        String catName = item.category != null ? item.category.name : "Tổng ngân sách";
        holder.tvCategory.setText(catName);
        
        double limit = item.budget.limitAmount;
        double spent = item.spent;
        // Tính toán phần trăm tiến độ
        int progress = (int) ((spent / limit) * 100);
        
        // Hiển thị số tiền đã chi / hạn mức (vd: 500k / 1M)
        holder.tvProgressText.setText(CurrencyUtils.format(spent) + " / " + CurrencyUtils.format(limit));
        holder.progressBar.setProgress(Math.min(progress, 100));
        
        // Thay đổi màu sắc thanh tiến độ: Đỏ nếu vượt định mức (>=100%), Xanh nếu còn trong định mức
        if (progress >= 100) {
            holder.progressBar.setProgressDrawable(holder.itemView.getContext().getDrawable(R.drawable.progress_red));
        } else {
            holder.progressBar.setProgressDrawable(holder.itemView.getContext().getDrawable(R.drawable.progress_blue));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvProgressText;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_budget_category);
            tvProgressText = itemView.findViewById(R.id.tv_budget_progress_text);
            progressBar = itemView.findViewById(R.id.progress_budget);
        }
    }
}

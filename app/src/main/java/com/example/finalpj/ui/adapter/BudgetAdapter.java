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

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
    private List<BudgetWithProgress> items = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetWithProgress item = items.get(position);
        String catName = item.category != null ? item.category.name : "Tổng ngân sách";
        holder.tvCategory.setText(catName);
        
        double limit = item.budget.limitAmount;
        double spent = item.spent;
        int progress = (int) ((spent / limit) * 100);
        
        holder.tvProgressText.setText(CurrencyUtils.format(spent) + " / " + CurrencyUtils.format(limit));
        holder.progressBar.setProgress(Math.min(progress, 100));
        
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

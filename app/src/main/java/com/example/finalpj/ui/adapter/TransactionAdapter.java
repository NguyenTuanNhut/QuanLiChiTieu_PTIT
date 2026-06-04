package com.example.finalpj.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.TransactionWithCategory;
import com.example.finalpj.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionWithCategory> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<TransactionWithCategory> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionWithCategory item = transactions.get(position);
        Transaction t = item.transaction;

        // Hiển thị tên danh mục
        if (item.category != null) {
            holder.tvCategoryName.setText(item.category.name);
            
            // Hiển thị icon
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    item.category.icon, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.imgCategory.setImageResource(resId);
            }
            
            // Vẽ hình tròn nền icon
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(item.category.color));
            holder.imgCategory.setBackground(bg);
            holder.imgCategory.setPadding(15, 15, 15, 15);
        } else {
            holder.tvCategoryName.setText(t.note != null && !t.note.isEmpty() ? t.note : "Giao dịch " + t.id);
        }

        holder.tvNote.setText(t.note != null ? t.note : (t.type.equals("EXPENSE") ? "Chi tiêu" : "Thu nhập"));

        String amountStr = CurrencyUtils.format(t.amount);
        if (t.type.equals("EXPENSE")) {
            holder.tvAmount.setText("-" + amountStr);
            holder.tvAmount.setTextColor(Color.parseColor("#FF5252"));
        } else {
            holder.tvAmount.setText("+" + amountStr);
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvNote, tvAmount;
        ImageView imgCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            imgCategory = itemView.findViewById(R.id.img_category);
        }
    }
}

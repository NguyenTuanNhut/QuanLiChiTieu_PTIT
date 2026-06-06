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

/**
 * Adapter quản lý việc hiển thị danh sách các Giao dịch.
 * Kết nối dữ liệu từ Database vào các phần tử của RecyclerView.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionWithCategory> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    // Interface để xử lý sự kiện khi người dùng nhấn vào một dòng giao dịch
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    // Cập nhật danh sách dữ liệu mới và làm mới giao diện
    public void setTransactions(List<TransactionWithCategory> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public TransactionWithCategory getTransactionAt(int position) {
        return transactions.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp layout cho từng dòng giao dịch
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionWithCategory item = transactions.get(position);
        Transaction t = item.transaction;

        // Hiển thị tên danh mục và icon nếu có
        if (item.category != null) {
            holder.tvCategoryName.setText(item.category.name);
            
            // Tìm icon từ tài nguyên (drawable) dựa trên tên icon lưu trong DB
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                    item.category.icon, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.imgCategory.setImageResource(resId);
            }
            
            // Tạo hình nền tròn màu sắc cho icon dựa trên mã màu của danh mục
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(item.category.color));
            holder.imgCategory.setBackground(bg);
            holder.imgCategory.setPadding(15, 15, 15, 15);
        } else {
            // Nếu không có danh mục, hiển thị ghi chú hoặc ID mặc định
            holder.tvCategoryName.setText(t.note != null && !t.note.isEmpty() ? t.note : "Giao dịch " + t.id);
        }

        // Hiển thị ghi chú của giao dịch
        holder.tvNote.setText(t.note != null ? t.note : (t.type.equals("EXPENSE") ? "Chi tiêu" : "Thu nhập"));

        // Định dạng và hiển thị số tiền theo tiền tệ (VND)
        String amountStr = CurrencyUtils.format(t.amount);
        if (t.type.equals("EXPENSE")) {
            // Nếu là Chi tiêu: Hiển thị dấu (-) và màu Đỏ
            holder.tvAmount.setText("-" + amountStr);
            holder.tvAmount.setTextColor(Color.parseColor("#FF5252"));
        } else {
            // Nếu là Thu nhập: Hiển thị dấu (+) và màu Xanh
            holder.tvAmount.setText("+" + amountStr);
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Xử lý sự kiện click vào item
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

    /**
     * Lớp ViewHolder giữ các tham chiếu đến các thành phần giao diện của một dòng.
     */
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

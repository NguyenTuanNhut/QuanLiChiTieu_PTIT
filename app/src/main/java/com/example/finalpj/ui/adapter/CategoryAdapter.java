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
import com.example.finalpj.data.db.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách các Danh mục (Category) dưới dạng Grid.
 * Dùng trong màn hình Thêm giao dịch để người dùng chọn loại chi tiêu/thu nhập.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;
    private int selectedCategoryId = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedCategoryId(int categoryId) {
        this.selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp layout cho từng item danh mục
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categories.get(position);
        holder.tvName.setText(cat.name);

        // Hiển thị icon tương ứng với danh mục
        int resId = holder.itemView.getContext().getResources().getIdentifier(
                cat.icon, "drawable", holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.imgIcon.setImageResource(resId);
        }

        // Tạo hình nền tròn với màu sắc đặc trưng của danh mục
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(Color.parseColor(cat.color));
        holder.viewBg.setBackground(bg);

        // Hiển thị trạng thái được chọn (Highlight)
        if (cat.id == selectedCategoryId) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD")); // Màu xanh nhạt khi chọn
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Xử lý sự kiện khi nhấn chọn danh mục
        holder.itemView.setOnClickListener(v -> {
            selectedCategoryId = cat.id;
            notifyDataSetChanged(); // Vẽ lại danh sách để cập nhật trạng thái chọn
            if (listener != null)
                listener.onCategoryClick(cat);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewBg;
        ImageView imgIcon;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewBg = itemView.findViewById(R.id.view_bg);
            imgIcon = itemView.findViewById(R.id.img_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}

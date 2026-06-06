package com.example.finalpj.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalpj.R;

/**
 * Lớp hỗ trợ thao tác vuốt để Xóa hoặc Sửa trên RecyclerView.
 */
public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final ColorDrawable background;
    private final int backgroundColor = Color.parseColor("#f44336"); // Màu đỏ cho xóa
    private final Paint clearPaint;

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        background = new ColorDrawable();
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Không hỗ trợ kéo thả đổi vị trí
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        
        if (dX == 0f && !isCurrentlyActive) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Vẽ nền màu khi vuốt
        background.setColor(dX > 0 ? Color.parseColor("#4CAF50") : backgroundColor); // Xanh nếu vuốt phải (Sửa), Đỏ nếu vuốt trái (Xóa)
        background.setBounds(
                dX > 0 ? itemView.getLeft() : itemView.getRight() + (int) dX,
                itemView.getTop(),
                dX > 0 ? itemView.getLeft() + (int) dX : itemView.getRight(),
                itemView.getBottom()
        );
        background.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }
}

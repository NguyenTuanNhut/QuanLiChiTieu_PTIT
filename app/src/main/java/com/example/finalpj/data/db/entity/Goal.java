package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'goals' (Mục tiêu tiết kiệm).
 */
@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Tên mục tiêu (vd: Mua Laptop, Đi du lịch)
    public String name;

    // Số tiền mục tiêu cần đạt được
    public double targetAmount;

    // Số tiền hiện tại đã tích lũy được
    public double currentAmount;

    // Ngày hạn chót (Deadline)
    public long deadline;

    // Màu sắc đại diện cho mục tiêu
    public String color;

    // Icon đại diện
    public String icon;

    // ID của người dùng sở hữu mục tiêu này
    @ColumnInfo(name = "user_id")
    public int userId;

    public Goal() {}

    public Goal(String name, double targetAmount, double currentAmount, long deadline, String color, String icon) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.color = color;
        this.icon = icon;
    }

    /**
     * Tính toán phần trăm hoàn thành.
     */
    public int getProgress() {
        if (targetAmount <= 0) return 0;
        int progress = (int) ((currentAmount / targetAmount) * 100);
        return Math.min(progress, 100);
    }
}

package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'budgets' (Ngân sách).
 * Giúp người dùng đặt ra hạn mức chi tiêu cho từng tháng hoặc từng danh mục cụ thể.
 */
@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Tháng áp dụng ngân sách
    public int month;
    
    // Năm áp dụng ngân sách
    public int year;
    
    // Số tiền hạn mức tối đa (vd: 5.000.000đ)
    public double limitAmount;

    // ID của danh mục áp dụng ngân sách. Nếu là NULL thì đây là hạn mức tổng cho cả tháng.
    @ColumnInfo(name = "category_id")
    public Integer categoryId;

    // ID của người dùng sở hữu ngân sách này
    @ColumnInfo(name = "user_id")
    public int userId;
}

package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'transactions' trong Database.
 * Đại diện cho một giao dịch tài chính (Thu hoặc Chi).
 */
@Entity(tableName = "transactions")
public class Transaction {
    // Tự động sinh ID tăng dần cho mỗi giao dịch
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Số tiền của giao dịch
    @ColumnInfo(name = "amount")
    public double amount;

    // Phân loại: "INCOME" (Thu nhập) hoặc "EXPENSE" (Chi tiêu)
    @ColumnInfo(name = "type")
    public String type;

    // ID của danh mục (liên kết với bảng categories)
    @ColumnInfo(name = "category_id")
    public int categoryId;

    // ID của tài khoản (liên kết với bảng accounts)
    @ColumnInfo(name = "account_id")
    public Integer accountId;

    // ID của người dùng sở hữu giao dịch này
    @ColumnInfo(name = "user_id")
    public int userId;

    // Ghi chú cụ thể cho giao dịch (vd: Ăn phở sáng)
    @ColumnInfo(name = "note")
    public String note;

    // Ngày thực hiện giao dịch (lưu dưới dạng timestamp - miliseconds)
    @ColumnInfo(name = "date")
    public long date;

    // Thời gian bản ghi này được tạo ra
    @ColumnInfo(name = "created_at")
    public long createdAt;
}

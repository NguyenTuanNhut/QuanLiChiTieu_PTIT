package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'recurring_transactions' (Giao dịch lặp lại).
 * Dùng cho các khoản thu/chi cố định hàng tháng như: Tiền nhà, Tiền mạng, Lương cố định...
 */
@Entity(tableName = "recurring_transactions")
public class RecurringTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Số tiền của giao dịch lặp lại
    @ColumnInfo(name = "amount")
    public double amount;

    // "INCOME" hoặc "EXPENSE"
    @ColumnInfo(name = "type")
    public String type;

    // Liên kết với danh mục
    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "note")
    public String note;

    // Ngày bắt đầu chu kỳ lặp lại
    @ColumnInfo(name = "start_date")
    public long startDate;

    // Ngày kết thúc (nếu có)
    @ColumnInfo(name = "end_date")
    public Long endDate;

    // Loại chu kỳ (Hàng ngày, Hàng tuần, Hàng tháng...)
    @ColumnInfo(name = "interval_type")
    public String intervalType;

    // Giá trị chu kỳ (vd: lặp lại mỗi 2 tháng)
    @ColumnInfo(name = "interval_value")
    public int intervalValue;

    // Thời điểm dự kiến tiếp theo sẽ thực hiện giao dịch này
    @ColumnInfo(name = "next_run_date")
    public Long nextRunDate;

    // Trạng thái bật/tắt của giao dịch lặp lại
    @ColumnInfo(name = "is_active")
    public int isActive; // 1: Đang bật, 0: Đã tắt

    public RecurringTransaction() {
    }
}

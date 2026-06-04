package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recurring_transactions")
public class RecurringTransaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "type")
    public String type; // INCOME or EXPENSE

    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "start_date")
    public long startDate;

    @ColumnInfo(name = "end_date")
    public Long endDate; // nullable

    @ColumnInfo(name = "interval_type")
    public String intervalType; // DAILY, WEEKLY, MONTHLY

    @ColumnInfo(name = "interval_value")
    public int intervalValue; // number of intervals

    @ColumnInfo(name = "next_run_date")
    public Long nextRunDate;

    @ColumnInfo(name = "is_active")
    public int isActive; // 1 active, 0 inactive

    public RecurringTransaction() {
    }
}

package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int month;
    public int year;
    public double limitAmount;    // hạn mức

    @ColumnInfo(name = "category_id")
    public Integer categoryId;    // null = hạn mức tổng tháng
}

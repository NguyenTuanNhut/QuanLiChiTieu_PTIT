package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;

public class CategoryExpense {
    @ColumnInfo(name = "categoryName")
    public String categoryName;

    @ColumnInfo(name = "total")
    public double total;
}

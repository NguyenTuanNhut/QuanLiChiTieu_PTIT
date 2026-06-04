package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "amount")
    public double amount; // số tiền

    @ColumnInfo(name = "type")
    public String type; // "INCOME" hoặc "EXPENSE"

    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "account_id")
    public Integer accountId;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "date")
    public long date; // timestamp (milliseconds)

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "type")
    public String type; // e.g., CASH, CARD, BANK

    @ColumnInfo(name = "balance")
    public double balance;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "color")
    public String color;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public Account() {
    }

    @Ignore
    public Account(String name, String type, double balance, String icon, String color, long createdAt) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.icon = icon;
        this.color = color;
        this.createdAt = createdAt;
    }
}

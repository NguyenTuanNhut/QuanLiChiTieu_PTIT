package com.example.finalpj.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;    // "Ăn uống", "Di chuyển"...
    public String icon;    // tên icon resource
    public String color;   // mã màu hex "#FF5733"
    public String type;    // "INCOME" hoặc "EXPENSE"

    public Category() {}

    public Category(String name, String icon, String color, String type) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
    }
}

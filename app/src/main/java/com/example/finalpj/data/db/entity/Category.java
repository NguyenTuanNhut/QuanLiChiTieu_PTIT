package com.example.finalpj.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'categories' (Danh mục).
 * Giúp phân loại các giao dịch như: Ăn uống, Lương, Giải trí...
 */
@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Tên hiển thị của danh mục (vd: Ăn uống)
    public String name;

    // Tên file icon trong thư mục drawable (vd: ic_food)
    public String icon;

    // Mã màu Hex để hiển thị giao diện (vd: #FF5252)
    public String color;

    // Phân loại danh mục thuộc nhóm Thu nhập hay Chi tiêu
    public String type;

    public Category() {}

    public Category(String name, String icon, String color, String type) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.type = type;
    }
}

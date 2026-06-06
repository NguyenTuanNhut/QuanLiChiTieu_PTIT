package com.example.finalpj.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Lớp Entity định nghĩa bảng 'accounts' (Tài khoản).
 * Cho phép người dùng quản lý nhiều nguồn tiền khác nhau (vd: Tiền mặt, Thẻ ngân hàng, Ví điện tử).
 */
@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Tên tài khoản (vd: VCB Bank, Ví MoMo)
    @ColumnInfo(name = "name")
    public String name;

    // Loại tài khoản (CASH: Tiền mặt, BANK: Ngân hàng...)
    @ColumnInfo(name = "type")
    public String type;

    // Số dư hiện có trong tài khoản
    @ColumnInfo(name = "balance")
    public double balance;

    // Icon hiển thị cho tài khoản
    @ColumnInfo(name = "icon")
    public String icon;

    // Màu sắc nhận diện tài khoản trên giao diện
    @ColumnInfo(name = "color")
    public String color;

    // Thời gian tạo tài khoản
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

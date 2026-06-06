package com.example.finalpj.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Lớp POJO (Plain Old Java Object) giúp Room thực hiện câu lệnh JOIN dữ liệu.
 * Kết hợp một Giao dịch (Transaction) với Danh mục (Category) tương ứng của nó.
 */
public class TransactionWithCategory {
    // Nhúng toàn bộ các trường của bảng Transaction vào đây
    @Embedded
    public Transaction transaction;

    // Định nghĩa mối quan hệ 1-1 (Một giao dịch thuộc về một danh mục)
    // Room sẽ tự động lấy dữ liệu từ bảng categories dựa trên khóa ngoại category_id
    @Relation(
            parentColumn = "category_id", // Cột trong bảng Transaction
            entityColumn = "id"           // Cột tương ứng trong bảng Category
    )
    public Category category;
}

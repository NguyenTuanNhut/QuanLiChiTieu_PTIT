package com.example.finalpj.data.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TransactionWithCategory {
    @Embedded
    public Transaction transaction;

    @Relation(
            parentColumn = "category_id",
            entityColumn = "id"
    )
    public Category category;
}

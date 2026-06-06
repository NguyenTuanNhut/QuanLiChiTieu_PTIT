package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.RecurringTransaction;

import java.util.List;

/**
 * Interface DAO để thao tác với bảng 'recurring_transactions'.
 */
@Dao
public interface RecurringTransactionDao {

    @Insert
    void insert(RecurringTransaction rt);

    @Update
    void update(RecurringTransaction rt);

    @Delete
    void delete(RecurringTransaction rt);

    // Lấy danh sách các giao dịch lặp lại đang ở trạng thái hoạt động (Active)
    @Query("SELECT * FROM recurring_transactions WHERE is_active = 1")
    LiveData<List<RecurringTransaction>> getActiveRecurring();

    @Query("SELECT * FROM recurring_transactions")
    List<RecurringTransaction> getAllSync();
}

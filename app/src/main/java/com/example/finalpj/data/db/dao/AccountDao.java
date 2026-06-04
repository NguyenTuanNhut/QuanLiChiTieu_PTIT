package com.example.finalpj.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalpj.data.db.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM accounts ORDER BY created_at DESC")
    LiveData<List<Account>> getAll();

    @Query("SELECT * FROM accounts")
    List<Account> getAllSync();
}

package com.example.finalpj.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.finalpj.data.db.dao.BudgetDao;
import com.example.finalpj.data.db.dao.CategoryDao;
import com.example.finalpj.data.db.dao.TransactionDao;
import com.example.finalpj.data.db.dao.AccountDao;
import com.example.finalpj.data.db.dao.RecurringTransactionDao;
import com.example.finalpj.data.db.entity.Budget;
import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.Account;
import com.example.finalpj.data.db.entity.RecurringTransaction;

import java.util.concurrent.Executors;

/**
 * Lớp cấu hình Database chính của ứng dụng sử dụng thư viện Room.
 * Định nghĩa các bảng (Entities) và phiên bản của Database.
 */
@Database(entities = { Transaction.class, Category.class, Budget.class, Account.class,
        RecurringTransaction.class }, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    // Các phương thức trừu tượng để lấy các đối tượng DAO
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();
    public abstract AccountDao accountDao();
    public abstract RecurringTransactionDao recurringTransactionDao();

    /**
     * Phương thức Singleton để khởi tạo và lấy instance duy nhất của Database.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "expense_tracker_db")
                    .addMigrations(MIGRATION_1_2) // Hỗ trợ nâng cấp DB từ v1 lên v2
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Khi database được tạo lần đầu, tự động thêm các danh mục mặc định
                            Executors.newSingleThreadExecutor().execute(() -> populateDefaultCategories(INSTANCE));
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Cấu hình Migration để nâng cấp Database từ phiên bản 1 lên phiên bản 2.
     * Thêm bảng tài khoản (accounts) và bảng giao dịch định kỳ (recurring_transactions).
     */
    private static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Tạo bảng accounts
            database.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`type` TEXT, " +
                    "`balance` REAL NOT NULL, " +
                    "`icon` TEXT, " +
                    "`color` TEXT, " +
                    "`created_at` INTEGER NOT NULL)");

            // Thêm cột account_id vào bảng transactions để liên kết
            database.execSQL("ALTER TABLE transactions ADD COLUMN account_id INTEGER");

            // Tạo bảng recurring_transactions cho các giao dịch lặp lại
            database.execSQL("CREATE TABLE IF NOT EXISTS `recurring_transactions` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`amount` REAL NOT NULL, " +
                    "`type` TEXT, " +
                    "`category_id` INTEGER NOT NULL, " +
                    "`note` TEXT, " +
                    "`start_date` INTEGER NOT NULL, " +
                    "`end_date` INTEGER, " +
                    "`interval_type` TEXT, " +
                    "`interval_value` INTEGER NOT NULL, " +
                    "`next_run_date` INTEGER, " +
                    "`is_active` INTEGER NOT NULL)");
        }
    };

    /**
     * Hàm hỗ trợ nạp dữ liệu danh mục ban đầu cho ứng dụng.
     */
    private static void populateDefaultCategories(AppDatabase db) {
        CategoryDao dao = db.categoryDao();
        // Nhóm các danh mục Chi tiêu (EXPENSE)
        dao.insert(new Category("Ăn uống", "ic_food", "#FF5252", "EXPENSE"));
        dao.insert(new Category("Di chuyển", "ic_transport", "#2196F3", "EXPENSE"));
        dao.insert(new Category("Mua sắm", "ic_shopping", "#4CAF50", "EXPENSE"));
        dao.insert(new Category("Giải trí", "ic_game", "#FF9800", "EXPENSE"));
        dao.insert(new Category("Sức khoẻ", "ic_health", "#E91E63", "EXPENSE"));
        dao.insert(new Category("Hoá đơn", "ic_bill", "#9C27B0", "EXPENSE"));
        dao.insert(new Category("Giáo dục", "ic_edu", "#00BCD4", "EXPENSE"));
        dao.insert(new Category("Khác", "ic_other", "#607D8B", "EXPENSE"));
        // Nhóm các danh mục Thu nhập (INCOME)
        dao.insert(new Category("Lương", "ic_salary", "#4CAF50", "INCOME"));
        dao.insert(new Category("Làm thêm", "ic_work", "#8BC34A", "INCOME"));
        dao.insert(new Category("Quà tặng", "ic_gift", "#FFC107", "INCOME"));
        dao.insert(new Category("Đầu tư", "ic_invest", "#00BCD4", "INCOME"));
    }
}

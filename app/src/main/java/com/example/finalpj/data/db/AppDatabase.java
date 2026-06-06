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
import com.example.finalpj.data.db.dao.GoalDao;
import com.example.finalpj.data.db.dao.UserDao;
import com.example.finalpj.data.db.entity.Budget;
import com.example.finalpj.data.db.entity.Category;
import com.example.finalpj.data.db.entity.Transaction;
import com.example.finalpj.data.db.entity.Account;
import com.example.finalpj.data.db.entity.RecurringTransaction;
import com.example.finalpj.data.db.entity.Goal;
import com.example.finalpj.data.db.entity.User;

import java.util.concurrent.Executors;

/**
 Tóm lại, AppDatabase là lớp quản lý toàn bộ dữ liệu của ứng dụng. Lớp này chịu trách nhiệm khởi tạo cơ sở dữ liệu,
 quản lý các bảng, hỗ trợ nâng cấp phiên bản bằng Migration và cung cấp các DAO để các tầng
 khác trong hệ thống truy cập dữ liệu.
 */
@Database(entities = { Transaction.class, Category.class, Budget.class, Account.class,
        RecurringTransaction.class, Goal.class, User.class }, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();
    public abstract AccountDao accountDao();
    public abstract RecurringTransactionDao recurringTransactionDao();
    public abstract GoalDao goalDao();
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "expense_tracker_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> populateDefaultCategories(INSTANCE));
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    private static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`type` TEXT, " +
                    "`balance` REAL NOT NULL, " +
                    "`icon` TEXT, " +
                    "`color` TEXT, " +
                    "`created_at` INTEGER NOT NULL)");
            database.execSQL("ALTER TABLE transactions ADD COLUMN account_id INTEGER");
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

    private static final androidx.room.migration.Migration MIGRATION_2_3 = new androidx.room.migration.Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `goals` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`targetAmount` REAL NOT NULL, " +
                    "`currentAmount` REAL NOT NULL, " +
                    "`deadline` INTEGER NOT NULL, " +
                    "`color` TEXT, " +
                    "`icon` TEXT)");
        }
    };

    private static final androidx.room.migration.Migration MIGRATION_3_4 = new androidx.room.migration.Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `users` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`fullName` TEXT, " +
                    "`email` TEXT, " +
                    "`password` TEXT, " +
                    "`avatarUrl` TEXT)");
        }
    };

    private static final androidx.room.migration.Migration MIGRATION_4_5 = new androidx.room.migration.Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE transactions ADD COLUMN user_id INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE budgets ADD COLUMN user_id INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE goals ADD COLUMN user_id INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static void populateDefaultCategories(AppDatabase db) {
        CategoryDao dao = db.categoryDao();
        dao.insert(new Category("Ăn uống", "ic_food", "#FF5252", "EXPENSE"));
        dao.insert(new Category("Di chuyển", "ic_transport", "#2196F3", "EXPENSE"));
        dao.insert(new Category("Mua sắm", "ic_shopping", "#4CAF50", "EXPENSE"));
        dao.insert(new Category("Giải trí", "ic_game", "#FF9800", "EXPENSE"));
        dao.insert(new Category("Sức khoẻ", "ic_health", "#E91E63", "EXPENSE"));
        dao.insert(new Category("Hoá đơn", "ic_bill", "#9C27B0", "EXPENSE"));
        dao.insert(new Category("Giáo dục", "ic_edu", "#00BCD4", "EXPENSE"));
        dao.insert(new Category("Tiết kiệm", "ic_goal", "#2196F3", "EXPENSE"));
        dao.insert(new Category("Khác", "ic_other", "#607D8B", "EXPENSE"));
        dao.insert(new Category("Lương", "ic_salary", "#4CAF50", "INCOME"));
        dao.insert(new Category("Làm thêm", "ic_work", "#8BC34A", "INCOME"));
        dao.insert(new Category("Quà tặng", "ic_gift", "#FFC107", "INCOME"));
        dao.insert(new Category("Đầu tư", "ic_invest", "#00BCD4", "INCOME"));
    }
}

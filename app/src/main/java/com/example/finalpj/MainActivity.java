package com.example.finalpj;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.example.finalpj.ui.add.AddTransactionActivity;
import com.example.finalpj.ui.auth.LoginActivity;
import com.example.finalpj.data.repository.AuthRepository;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.finalpj.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.finalpj.utils.LocaleHelper;
import android.content.Context;
import androidx.annotation.NonNull;

/**
 * MainActivity: Activity chính của ứng dụng, đóng vai trò là container cho các Fragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AuthRepository authRepository;

    //Khi ứng dụng được mở, phương thức onCreate() sẽ được gọi đầu tiên để khởi tạo giao diện
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        authRepository = new AuthRepository(getApplication());
        if (!authRepository.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Tại đây em sử dụng EdgeToEdge để giao diện hiển thị tràn viền, tận dụng tối đa không gian màn hình
        EdgeToEdge.enable(this);

        // Tiếp theo em sử dụng ViewBinding để ánh xạ các thành phần giao diện thay vì dùng findViewById(), giúp code ngắn gọn và hạn chế lỗi.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Xử lý WindowInsets để tránh UI bị đè bởi thanh trạng thái hoặc thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Đoạn này dùng để thiết lập Toolbar làm ActionBar của ứng dụng
        setSupportActionBar(binding.toolbar);

        // Tại đây em khởi tạo NavController. Thành phần này chịu trách nhiệm quản lý việc điều hướng giữa các Fragment trong ứng dụng
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();
        
        // Ở đây em khai báo các màn hình chính của ứng dụng
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomeFragment, R.id.HistoryFragment, R.id.GoalsFragment, R.id.StatsFragment, R.id.ProfileFragment)
                .build();

        // Đoạn code này đồng bộ ActionBar với Navigation Component
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //Đây là đoạn code kết nối Bottom Navigation với NavController. Khi người dùng chọn một mục ở thanh điều hướng phía dưới, hệ thống sẽ tự động chuyển sang Fragment tương ứng
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // Thay đổi hành vi của FAB dựa trên Fragment hiện tại
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomeFragment || destination.getId() == R.id.HistoryFragment) {
                binding.fab.setImageResource(android.R.drawable.ic_input_add);
                binding.fab.show();
                binding.fab.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                    startActivity(intent);
                });
            } else if (destination.getId() == R.id.GoalsFragment) {
                binding.fab.setImageResource(android.R.drawable.ic_input_add);
                binding.fab.show();
                binding.fab.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, com.example.finalpj.ui.goals.AddGoalActivity.class);
                    startActivity(intent);
                });
            } else {
                binding.fab.hide();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Nạp menu vào ActionBar (nếu có)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.action_theme);
        if (themeItem != null) {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                // Đang ở chế độ tối -> hiện icon mặt trời (để chuyển sang sáng)
                themeItem.setIcon(R.drawable.light_mode_24px);
                themeItem.setTitle("Chế độ sáng");
            } else {
                // Đang ở chế độ sáng -> hiện icon mặt trăng (để chuyển sang tối)
                themeItem.setIcon(R.drawable.mode_night_24px);
                themeItem.setTitle("Chế độ tối");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý khi các item trên menu được chọn
        int id = item.getItemId();

        if (id == R.id.action_theme) {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        }

        if (id == R.id.action_language) {
            String currentLang = LocaleHelper.getLanguage(this);
            String newLang = currentLang.equals("vi") ? "en" : "vi";
            LocaleHelper.setLocale(this, newLang);
            recreate();
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        
        if (id == R.id.action_logout) {
            authRepository.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý nút quay lại (Back) trên ActionBar khi sử dụng Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}

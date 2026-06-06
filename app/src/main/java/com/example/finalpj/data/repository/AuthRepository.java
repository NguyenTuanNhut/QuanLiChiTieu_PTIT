package com.example.finalpj.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.example.finalpj.data.db.AppDatabase;
import com.example.finalpj.data.db.dao.UserDao;
import com.example.finalpj.data.db.entity.User;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthRepository {
    private UserDao userDao;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";

    public AuthRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean register(String name, String email, String password) {
        if (userDao.getUserByEmail(email) != null) return false;

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = new User(name, email, hashedPassword);
        userDao.insert(user);
        return true;
    }

    public boolean login(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user == null) return false;

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.password);
        if (result.verified) {
            // Giả lập lưu JWT Token sau khi login thành công
            saveToken("mock_jwt_token_for_" + user.id, user.id);
            return true;
        }
        return false;
    }

    private void saveToken(String token, int userId) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_TOKEN);
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public void updateUser(User user) {
        userDao.update(user);
    }

    public User getCurrentUser() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) return null;
        return userDao.getUserById(userId);
    }

    public LiveData<User> getCurrentUserLive() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        return userDao.getUserByIdLive(userId);
    }

    public int getCurrentUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }
}

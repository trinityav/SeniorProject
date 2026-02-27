package com.example.myapplication.data.repo;

import android.content.Context;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.DatabaseProvider;
import com.example.myapplication.data.local.UserDao;
import com.example.myapplication.data.local.UserEntity;

public class FitnessRepository {

    private final UserDao userDao;

    public FitnessRepository(Context context) {
        AppDatabase db = DatabaseProvider.getDatabase(context);
        userDao = db.userDao();
    }

    public long createUser(String username, String email, String password) {
        UserEntity user = new UserEntity();
        user.username = username;
        user.email = email;
        user.password = password;
        return userDao.insertUser(user);
    }

    public UserEntity getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public UserEntity getUserByEmailAndPassword(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, password);
    }

    public void ensureAdminUserExists() {
        String adminEmail = "admin@gmail.com";
        String adminPassword = "admin@1234";

        UserEntity existing = userDao.getUserByEmail(adminEmail);
        if (existing == null) {
            UserEntity admin = new UserEntity();
            admin.username = "Admin";
            admin.email = adminEmail;
            admin.password = adminPassword;
            userDao.insertUser(admin);
        }
    }
}
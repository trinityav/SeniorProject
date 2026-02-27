package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "fitness_session";
    private static final String KEY_USER_ID = "logged_in_user_id";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void loginUser(long userId) {
        editor.putLong(KEY_USER_ID, userId);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_USER_ID);
    }

    public long getLoggedInUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }
}
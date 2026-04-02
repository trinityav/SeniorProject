package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

// Stores login token locally
public class SessionManager {

    private static final String PREF_NAME = "fitness_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USERNAME = "username";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save token after successful login
    public void saveLoginSession(String accessToken, String username) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // Clear session on logout
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    // User is logged in if token exists
    public boolean isLoggedIn() {
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        return token != null && !token.isEmpty();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }
}
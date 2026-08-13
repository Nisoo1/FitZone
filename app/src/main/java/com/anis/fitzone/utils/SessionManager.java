package com.anis.fitzone.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.anis.fitzone.modeles.User;

/**
 * Conserve l'utilisateur connecté dans les SharedPreferences pour éviter de
 * redemander le login à chaque ouverture de l'application.
 */
public class SessionManager {

    private static final String PREFS_NAME = "fitzone_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PRENOM = "prenom";
    private static final String KEY_NOM = "nom";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_PHOTO_URL = "photo_url";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(User user) {
        prefs.edit()
                .putString(KEY_USER_ID, user.getId())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_PRENOM, user.getPrenom())
                .putString(KEY_NOM, user.getNom())
                .putString(KEY_TELEPHONE, user.getTelephone())
                .putString(KEY_PHOTO_URL, user.getPhotoUrl())
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getPrenom() {
        return prefs.getString(KEY_PRENOM, "");
    }

    public String getNom() {
        return prefs.getString(KEY_NOM, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getTelephone() {
        return prefs.getString(KEY_TELEPHONE, "");
    }

    public String getPhotoUrl() {
        return prefs.getString(KEY_PHOTO_URL, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

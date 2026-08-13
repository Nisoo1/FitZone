package com.anis.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Journal local de consultation (programmes, séances, quiz consultés),
 * conservé uniquement sur l'appareil (non synchronisé au serveur).
 */
public class HistoriqueDao {

    public static class Entree {
        public String type;
        public String refId;
        public String label;
        public String timestamp;
    }

    private final DBHelper dbHelper;

    public HistoriqueDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void log(String userId, String type, String refId, String label) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("type", type);
        values.put("refId", refId);
        values.put("label", label);
        values.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA).format(new Date()));
        db.insert(DBHelper.TABLE_HISTORIQUE, null, values);
    }

    public List<Entree> getRecent(String userId, int limit) {
        List<Entree> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_HISTORIQUE, null, "userId = ?",
                new String[]{userId}, null, null, "_id DESC", String.valueOf(limit))) {
            while (cursor.moveToNext()) {
                Entree e = new Entree();
                e.type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                e.refId = cursor.getString(cursor.getColumnIndexOrThrow("refId"));
                e.label = cursor.getString(cursor.getColumnIndexOrThrow("label"));
                e.timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                result.add(e);
            }
        }
        return result;
    }
}

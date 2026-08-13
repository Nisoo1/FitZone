package com.anis.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anis.fitzone.modeles.Program;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ProgramDao {

    private final DBHelper dbHelper;
    private final Gson gson = new Gson();

    public ProgramDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void saveAll(List<Program> programs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DBHelper.TABLE_PROGRAMS, null, null);
            for (Program p : programs) {
                ContentValues values = new ContentValues();
                values.put("id", p.getId());
                values.put("code", p.getCode());
                values.put("title", p.getTitle());
                values.put("description", p.getDescription());
                values.put("coach", p.getCoach());
                values.put("session", p.getSession());
                values.put("imageUrl", p.getImageUrl());
                values.put("annoncesJson", gson.toJson(p.getAnnonces()));
                db.insertWithOnConflict(DBHelper.TABLE_PROGRAMS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Program> getAll() {
        List<Program> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_PROGRAMS, null, null, null, null, null, "title ASC")) {
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
        }
        return result;
    }

    public Program getById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_PROGRAMS, null, "id = ?", new String[]{id}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    private Program fromCursor(Cursor cursor) {
        Program p = new Program();
        p.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        p.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
        p.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        p.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        p.setCoach(cursor.getString(cursor.getColumnIndexOrThrow("coach")));
        p.setSession(cursor.getString(cursor.getColumnIndexOrThrow("session")));
        p.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")));
        String annoncesJson = cursor.getString(cursor.getColumnIndexOrThrow("annoncesJson"));
        if (annoncesJson != null) {
            List<String> annonces = gson.fromJson(annoncesJson, new TypeToken<List<String>>() {
            }.getType());
            p.setAnnonces(annonces != null ? annonces : new ArrayList<>());
        }
        return p;
    }
}

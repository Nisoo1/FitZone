package com.anis.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anis.fitzone.modeles.NutritionTip;

import java.util.ArrayList;
import java.util.List;

public class NutritionDao {

    private final DBHelper dbHelper;

    public NutritionDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void saveAll(List<NutritionTip> tips) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DBHelper.TABLE_NUTRITION, null, null);
            for (NutritionTip t : tips) {
                ContentValues values = new ContentValues();
                values.put("id", t.getId());
                values.put("aliment", t.getAliment());
                values.put("emoji", t.getEmoji());
                values.put("calories", t.getCalories());
                values.put("description", t.getDescription());
                values.put("moment", t.getMoment());
                db.insertWithOnConflict(DBHelper.TABLE_NUTRITION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<NutritionTip> getAll() {
        List<NutritionTip> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_NUTRITION, null, null, null, null, null, "aliment ASC")) {
            while (cursor.moveToNext()) {
                NutritionTip t = new NutritionTip();
                t.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                t.setAliment(cursor.getString(cursor.getColumnIndexOrThrow("aliment")));
                t.setEmoji(cursor.getString(cursor.getColumnIndexOrThrow("emoji")));
                t.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow("calories")));
                t.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                t.setMoment(cursor.getString(cursor.getColumnIndexOrThrow("moment")));
                result.add(t);
            }
        }
        return result;
    }
}

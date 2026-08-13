package com.anis.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anis.fitzone.modeles.QuizResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizResultDao {

    private final DBHelper dbHelper;

    public QuizResultDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void saveResult(String userId, String quizId, int score, int total) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("quizId", quizId);
        values.put("score", score);
        values.put("total", total);
        values.put("takenAt", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA).format(new Date()));
        db.insert(DBHelper.TABLE_QUIZ_RESULTS, null, values);
    }

    /** Retourne le meilleur résultat par quiz pour un utilisateur donné. */
    public Map<String, QuizResult> getBestResultsForUser(String userId) {
        Map<String, QuizResult> best = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_QUIZ_RESULTS, null, "userId = ?",
                new String[]{userId}, null, null, "_id ASC")) {
            while (cursor.moveToNext()) {
                String quizId = cursor.getString(cursor.getColumnIndexOrThrow("quizId"));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                best.put(quizId, new QuizResult(quizId, score, total));
            }
        }
        return best;
    }

    public List<QuizResult> getHistoryForUser(String userId) {
        List<QuizResult> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_QUIZ_RESULTS, null, "userId = ?",
                new String[]{userId}, null, null, "_id DESC")) {
            while (cursor.moveToNext()) {
                String quizId = cursor.getString(cursor.getColumnIndexOrThrow("quizId"));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                results.add(new QuizResult(quizId, score, total));
            }
        }
        return results;
    }
}

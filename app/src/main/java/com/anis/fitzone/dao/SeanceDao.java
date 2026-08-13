package com.anis.fitzone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anis.fitzone.modeles.Seance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeanceDao {

    private final DBHelper dbHelper;

    public SeanceDao(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    /**
     * Remplace le cache local par les données serveur, sauf pour les séances
     * dont une soumission locale n'a pas encore été confirmée par le serveur
     * (synced = 0) : celles-ci sont préservées pour ne pas perdre la saisie
     * de l'utilisateur en cas de reconnexion tardive.
     */
    public void saveAllForProgram(String programId, List<Seance> seances) {
        Set<String> unsyncedIds = getUnsyncedIds(programId);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Seance s : seances) {
                if (unsyncedIds.contains(s.getId())) {
                    continue;
                }
                db.insertWithOnConflict(DBHelper.TABLE_SEANCES, null, toValues(s, 1), SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void upsert(Seance s, boolean synced) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insertWithOnConflict(DBHelper.TABLE_SEANCES, null, toValues(s, synced ? 1 : 0), SQLiteDatabase.CONFLICT_REPLACE);
    }

    private Set<String> getUnsyncedIds(String programId) {
        Set<String> ids = new HashSet<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_SEANCES, new String[]{"id"},
                "programId = ? AND synced = 0", new String[]{programId}, null, null, null)) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
            }
        }
        return ids;
    }

    public boolean isSynced(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_SEANCES, new String[]{"synced"},
                "id = ?", new String[]{id}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 1;
            }
        }
        return true;
    }

    public List<Seance> getByProgram(String programId) {
        List<Seance> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_SEANCES, null, "programId = ?",
                new String[]{programId}, null, null, "dueDate ASC")) {
            while (cursor.moveToNext()) {
                result.add(fromCursor(cursor));
            }
        }
        return result;
    }

    public Seance getById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_SEANCES, null, "id = ?", new String[]{id}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    private ContentValues toValues(Seance s, int synced) {
        ContentValues values = new ContentValues();
        values.put("id", s.getId());
        values.put("programId", s.getProgramId());
        values.put("title", s.getTitle());
        values.put("description", s.getDescription());
        values.put("dueDate", s.getDueDate());
        values.put("instructions", s.getInstructions());
        values.put("status", s.getStatus());
        if (s.getGrade() != null) {
            values.put("grade", s.getGrade());
        } else {
            values.putNull("grade");
        }
        values.put("comment", s.getComment());
        values.put("totalPoints", s.getTotalPoints());
        values.put("type", s.getType());
        values.put("submissionText", s.getSubmissionText());
        values.put("submissionUrl", s.getSubmissionUrl());
        values.put("submittedAt", s.getSubmittedAt());
        values.put("synced", synced);
        return values;
    }

    private Seance fromCursor(Cursor cursor) {
        Seance s = new Seance();
        s.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        s.setProgramId(cursor.getString(cursor.getColumnIndexOrThrow("programId")));
        s.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        s.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        s.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow("dueDate")));
        s.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow("instructions")));
        s.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        int gradeIndex = cursor.getColumnIndexOrThrow("grade");
        s.setGrade(cursor.isNull(gradeIndex) ? null : cursor.getInt(gradeIndex));
        s.setComment(cursor.getString(cursor.getColumnIndexOrThrow("comment")));
        s.setTotalPoints(cursor.getInt(cursor.getColumnIndexOrThrow("totalPoints")));
        s.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        s.setSubmissionText(cursor.getString(cursor.getColumnIndexOrThrow("submissionText")));
        s.setSubmissionUrl(cursor.getString(cursor.getColumnIndexOrThrow("submissionUrl")));
        s.setSubmittedAt(cursor.getString(cursor.getColumnIndexOrThrow("submittedAt")));
        return s;
    }
}

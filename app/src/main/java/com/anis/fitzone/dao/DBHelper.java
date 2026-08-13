package com.anis.fitzone.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Base SQLite locale de FitZone.
 * Sert de cache hors ligne (programmes, séances, nutrition) et stocke
 * localement l'historique de consultation et les résultats de quiz.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fitzone.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PROGRAMS = "programs_cache";
    public static final String TABLE_SEANCES = "seances_cache";
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String TABLE_NUTRITION = "nutrition_cache";
    public static final String TABLE_HISTORIQUE = "historique";

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROGRAMS + " (" +
                "id TEXT PRIMARY KEY," +
                "code TEXT," +
                "title TEXT," +
                "description TEXT," +
                "coach TEXT," +
                "session TEXT," +
                "imageUrl TEXT," +
                "annoncesJson TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SEANCES + " (" +
                "id TEXT PRIMARY KEY," +
                "programId TEXT," +
                "title TEXT," +
                "description TEXT," +
                "dueDate TEXT," +
                "instructions TEXT," +
                "status TEXT," +
                "grade INTEGER," +
                "comment TEXT," +
                "totalPoints INTEGER," +
                "type TEXT," +
                "submissionText TEXT," +
                "submissionUrl TEXT," +
                "submittedAt TEXT," +
                "synced INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE " + TABLE_QUIZ_RESULTS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId TEXT," +
                "quizId TEXT," +
                "score INTEGER," +
                "total INTEGER," +
                "takenAt TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NUTRITION + " (" +
                "id TEXT PRIMARY KEY," +
                "aliment TEXT," +
                "emoji TEXT," +
                "calories INTEGER," +
                "description TEXT," +
                "moment TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HISTORIQUE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId TEXT," +
                "type TEXT," +
                "refId TEXT," +
                "label TEXT," +
                "timestamp TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEANCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIQUE);
        onCreate(db);
    }
}

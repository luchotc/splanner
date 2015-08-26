package com.savanto.splanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "splanner.db";
    private static final int VERSION = 1;

    private static final char COMMA = ',';
    private static final String TYPE_INT = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_GOALS = CREATE_TABLE + Schema.TABLE_GOALS + "("
            + Schema._ID + TYPE_INT + COMMA
            + Schema.FIELD_TEXT + TYPE_TEXT
            + ")";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";


    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (DatabaseHelper.instance == null) {
            DatabaseHelper.instance = new DatabaseHelper(context);
        }
        return DatabaseHelper.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GOALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + Schema.TABLE_GOALS);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }

    public String[] getGoals() {
        return new String[] { "a", "b", "c", "d" };
    }

    public String[] getTasks() {
        return new String[] { "task1", "task2", "task3", "task4" };
    }

    public String[] getDay() {
        return new String[] { "8.00 -- do stuff", "9.00 -- rest" };
    }


    private static final class Schema implements BaseColumns {
        private static final String TABLE_GOALS = "Goals";

        private static final String FIELD_TEXT = "text";
    }
}

package com.savanto.splanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "splanner.db";
    private static final int VERSION = 1;

    private static final char COMMA = ',';
    private static final String TYPE_INT = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String ASC = " ASC";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_GOALS = CREATE_TABLE + Schema.TABLE_GOALS + "("
            + Schema._ID + TYPE_INT + " PRIMARY KEY AUTOINCREMENT" + COMMA
            + Schema.FIELD_TEXT + TYPE_TEXT
            + ")";
    private static final String CREATE_TABLE_TASKS = CREATE_TABLE + Schema.TABLE_TASKS + "("
            + Schema._ID + TYPE_INT + " PRIMARY KEY AUTOINCREMENT" + COMMA
            + Schema.FIELD_GOAL_ID + TYPE_INT + COMMA
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
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + Schema.TABLE_GOALS);
        db.execSQL(DROP_TABLE + Schema.TABLE_TASKS);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }

    private Cursor select(String table, String[] columns, String whereClause, String[] whereArgs,
                          String orderBy, Integer limit) {
        final SQLiteDatabase db;
        try {
            db = this.getReadableDatabase();
            return db.query(
                    table,
                    columns,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    orderBy,
                    limit != null ? limit.toString() : null
            );
        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            return null;
        }
    }

    private boolean insert(String table, ContentValues values) {
        final SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            return false;
        }

        try {
            db.beginTransaction();
            db.insert(table, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
        }

        return true;
    }

    private boolean delete(String table, Long id) {
        final SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            return false;
        }

        try {
            db.beginTransaction();
            db.delete(table, Schema._ID + " = ?", new String[]{ id.toString() });
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
        }

        return true;
    }

    private static boolean moveToFirst(Cursor cursor) {
        try {
            return cursor.moveToFirst();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static long getLong(Cursor cursor, String column) {
        try {
            return cursor.getLong(cursor.getColumnIndexOrThrow(column));
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    private static String getString(Cursor cursor, String column) {
        try {
            return cursor.getString(cursor.getColumnIndexOrThrow(column));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Item[] getGoals() {
        final Cursor cursor = this.select(
                Schema.TABLE_GOALS,
                null,
                null,
                null,
                Schema.FIELD_TEXT + ASC,
                null
        );
        if (cursor != null && DatabaseHelper.moveToFirst(cursor)) {
            final Item[] goals = new Item[cursor.getCount()];
            do {
                goals[cursor.getPosition()] = new Item(
                        DatabaseHelper.getLong(cursor, Schema._ID),
                        DatabaseHelper.getString(cursor, Schema.FIELD_TEXT)
                );
            } while (cursor.moveToNext());

            return goals;
        } else {
            return null;
        }
    }

    public Item[] getTasks(long goalId) {
        final Cursor cursor = this.select(
                Schema.TABLE_TASKS,
                null,
                goalId != 0 ? Schema.FIELD_GOAL_ID + " = ?" : null,
                goalId != 0 ? new String[] { Long.toString(goalId) } : null,
                Schema.FIELD_TEXT + ASC,
                null
        );
        if (cursor != null && DatabaseHelper.moveToFirst(cursor)) {
            final Item[] tasks = new Item[cursor.getCount()];
            do {
                tasks[cursor.getPosition()] = new Item(
                        DatabaseHelper.getLong(cursor, Schema._ID),
                        DatabaseHelper.getString(cursor, Schema.FIELD_TEXT)
                );
            } while (cursor.moveToNext());

            return tasks;
        } else {
            return null;
        }
    }

    public String[] getDay() {
        return new String[] { "8.00 -- do stuff", "9.00 -- rest" };
    }

    public boolean insertGoal(String goal) {
        final ContentValues values = new ContentValues(1);
        values.put(Schema.FIELD_TEXT, goal);

        return this.insert(Schema.TABLE_GOALS, values);
    }

    public boolean insertTask(long goalId, String task) {
        final ContentValues values = new ContentValues(2);
        values.put(Schema.FIELD_GOAL_ID, goalId);
        values.put(Schema.FIELD_TEXT, task);

        return this.insert(Schema.TABLE_TASKS, values);
    }

    public boolean deleteGoal(long id) {
        return this.delete(Schema.TABLE_GOALS, id);
    }

    public boolean deleteTask(long id) {
        return this.delete(Schema.TABLE_TASKS, id);
    }


    private static final class Schema implements BaseColumns {
        private static final String TABLE_GOALS = "Goals";
        private static final String TABLE_TASKS = "Tasks";

        private static final String FIELD_TEXT = "text";
        private static final String FIELD_GOAL_ID = "goal_id";
    }
}

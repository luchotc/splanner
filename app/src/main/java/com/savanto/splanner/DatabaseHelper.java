package com.savanto.splanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


/* package */ final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "splanner.db";
    private static final int VERSION = 3;

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
    private static final String CREATE_TABLE_TIMES = CREATE_TABLE + Schema.TABLE_TIMES + "("
            + Schema._ID + TYPE_INT + " PRIMARY KEY AUTOINCREMENT" + COMMA
            + Schema.FIELD_GOAL_ID + TYPE_INT + COMMA
            + Schema.FIELD_TASK_ID + TYPE_INT + COMMA
            + Schema.FIELD_TIME + TYPE_INT + COMMA
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
        db.execSQL(CREATE_TABLE_TIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + Schema.TABLE_GOALS);
        db.execSQL(DROP_TABLE + Schema.TABLE_TASKS);
        db.execSQL(DROP_TABLE + Schema.TABLE_TIMES);
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
        return this.delete(table, Schema._ID + " = ?", new String[]{ id.toString() });
    }

    private boolean delete(String table, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            return false;
        }

        try {
            db.beginTransaction();
            db.delete(table, whereClause, whereArgs);
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

    public Model.Goal[] getGoals() {
        final Cursor cursor = this.select(
                Schema.TABLE_GOALS,
                null,
                null,
                null,
                Schema.FIELD_TEXT + ASC,
                null
        );
        if (cursor != null && DatabaseHelper.moveToFirst(cursor)) {
            final Model.Goal[] goals = new Model.Goal[cursor.getCount()];
            do {
                goals[cursor.getPosition()] = new Model.Goal(
                        DatabaseHelper.getLong(cursor, Schema._ID),
                        DatabaseHelper.getString(cursor, Schema.FIELD_TEXT)
                );
            } while (cursor.moveToNext());

            cursor.close();
            return goals;
        } else {
            return null;
        }
    }

    public Model.Task[] getTasks(Long goalId) {
        final Cursor cursor = this.select(
                Schema.TABLE_TASKS,
                null,
                goalId != 0 ? Schema.FIELD_GOAL_ID + " = ?" : null,
                goalId != 0 ? new String[] { goalId.toString() } : null,
                Schema.FIELD_TEXT + ASC,
                null
        );
        if (cursor != null && DatabaseHelper.moveToFirst(cursor)) {
            final Model.Task[] tasks = new Model.Task[cursor.getCount()];
            do {
                tasks[cursor.getPosition()] = new Model.Task(
                        DatabaseHelper.getLong(cursor, Schema._ID),
                        DatabaseHelper.getLong(cursor, Schema.FIELD_GOAL_ID),
                        DatabaseHelper.getString(cursor, Schema.FIELD_TEXT)
                );
            } while (cursor.moveToNext());

            cursor.close();
            return tasks;
        } else {
            return null;
        }
    }

    public Model.Time[] getTimes() {
        final Cursor cursor = this.select(
                Schema.TABLE_TIMES,
                null,
                null,
                null,
                Schema.FIELD_TIME + ASC,
                null
        );
        if (cursor != null && DatabaseHelper.moveToFirst(cursor)) {
            final Model.Time[] times = new Model.Time[cursor.getCount()];
            do {
                times[cursor.getPosition()] = new Model.Time(
                        DatabaseHelper.getLong(cursor, Schema._ID),
                        DatabaseHelper.getLong(cursor, Schema.FIELD_GOAL_ID),
                        DatabaseHelper.getLong(cursor, Schema.FIELD_TASK_ID),
                        DatabaseHelper.getLong(cursor, Schema.FIELD_TIME),
                        DatabaseHelper.getString(cursor, Schema.FIELD_TEXT)
                );
            } while (cursor.moveToNext());

            cursor.close();
            return times;
        }
        return null;
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

    public boolean insertTime( Model.Task task, long time) {
        final ContentValues values = new ContentValues(4);
        values.put(Schema.FIELD_GOAL_ID, task.goalId);
        values.put(Schema.FIELD_TASK_ID, task.id);
        values.put(Schema.FIELD_TIME, time);
        values.put(Schema.FIELD_TEXT, task.text);

        return this.insert(Schema.TABLE_TIMES, values);
    }

    public boolean deleteGoal(Long id) {
        return (this.delete(Schema.TABLE_GOALS, id)
                && this.delete(
                        Schema.TABLE_TASKS,
                        Schema.FIELD_GOAL_ID + " = ?",
                        new String[] { id.toString() }
                )
                && this.delete(
                        Schema.TABLE_TIMES,
                        Schema.FIELD_GOAL_ID + " = ?",
                        new String[] { id.toString() }
                )
        );
    }

    public boolean deleteTask(Long id) {
        return (this.delete(Schema.TABLE_TASKS, id)
                && this.delete(
                        Schema.TABLE_TIMES,
                        Schema.FIELD_TASK_ID + " = ?",
                        new String[] { id.toString() }
                )
        );
    }

    public boolean deleteTime(long id) {
        return this.delete(Schema.TABLE_TIMES, id);
    }


    public boolean clearTimes() {
        final SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (SQLiteException e) {
            return false;
        }

        try {
            db.beginTransaction();
            db.execSQL(DROP_TABLE + Schema.TABLE_TIMES);
            db.execSQL(CREATE_TABLE_TIMES);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
        }

        return true;
    }

    private static final class Schema implements BaseColumns {
        private static final String TABLE_GOALS = "Goals";
        private static final String TABLE_TASKS = "Tasks";
        private static final String TABLE_TIMES = "Times";

        private static final String FIELD_TEXT = "text";
        private static final String FIELD_GOAL_ID = "goal_id";
        private static final String FIELD_TASK_ID = "task_id";
        private static final String FIELD_TIME = "time";
    }
}

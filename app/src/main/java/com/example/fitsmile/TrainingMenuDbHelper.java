package com.example.fitsmile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrainingMenuDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "training_menu.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_TRAINING_MENU = "training_menu";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MENU_LIST = "menu_list";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_TRAINING_MENU + " (" +
            COLUMN_DATE + " TEXT PRIMARY KEY," +
            COLUMN_MENU_LIST + " TEXT" +
            ");";

    public TrainingMenuDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TRAINING_MENU + " ADD COLUMN " + COLUMN_MENU_LIST + " TEXT");
        }
    }
}

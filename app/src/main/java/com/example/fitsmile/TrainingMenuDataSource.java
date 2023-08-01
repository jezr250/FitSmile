package com.example.fitsmile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class TrainingMenuDataSource {

    private SQLiteDatabase database;
    private TrainingMenuDbHelper dbHelper;

    public TrainingMenuDataSource(Context context) {
        dbHelper = new TrainingMenuDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveTrainingMenu(String date, List<String> trainingMenuList) {
        ContentValues values = new ContentValues();
        values.put(TrainingMenuDbHelper.COLUMN_DATE, date);
        values.put(TrainingMenuDbHelper.COLUMN_MENU_LIST, convertListToString(trainingMenuList));

        // 既存のデータを更新するため、updateメソッドを使用
        int rowsAffected = database.update(
                TrainingMenuDbHelper.TABLE_TRAINING_MENU, // テーブル名
                values, // 更新するデータ
                TrainingMenuDbHelper.COLUMN_DATE + " = ?", // 更新対象の条件式
                new String[]{date} // 条件式のパラメータ
        );

        // 更新が行われなかった場合は新規に挿入
        if (rowsAffected <= 0) {
            long insertId = database.insert(TrainingMenuDbHelper.TABLE_TRAINING_MENU, null, values);
            if (insertId == -1) {
                // 新規挿入に失敗した場合のエラーログ
                Log.e("TrainingMenuDataSource", "Failed to save training menu to database.");
            }
        }
    }

    private String convertListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append(item).append(",");
        }
        return sb.toString();
    }

    private List<String> convertStringToList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    public List<String> getTrainingMenu(String date) {
        List<String> menuList = new ArrayList<>();
        String[] columns = {TrainingMenuDbHelper.COLUMN_MENU_LIST};
        String selection = TrainingMenuDbHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = {date};

        Cursor cursor = database.query(
                TrainingMenuDbHelper.TABLE_TRAINING_MENU,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String menuString = cursor.getString(cursor.getColumnIndex(TrainingMenuDbHelper.COLUMN_MENU_LIST));
            if (menuString != null) {
                menuList.addAll(Arrays.asList(menuString.split(",")));
            }
            cursor.close();
        }

        return menuList;
    }

    // データベースから指定した日付のトレーニングメニューを削除するメソッド
    public void deleteTrainingMenu(String date) {
        String selection = TrainingMenuDbHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = {date};
        database.delete(TrainingMenuDbHelper.TABLE_TRAINING_MENU, selection, selectionArgs);
    }
}
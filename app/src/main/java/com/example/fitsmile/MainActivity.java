package com.example.fitsmile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CalendarView;
import java.util.Arrays;
import android.widget.ListView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
public class MainActivity extends AppCompatActivity {

    private TrainingMenuDataSource dataSource;
    private Set<String> selectedTrainingMenuSet = new HashSet<>();
    private TextView registrationContentTextView;
    private CalendarView calendarView;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new TrainingMenuDataSource(this);
        dataSource.open();

        registrationContentTextView = findViewById(R.id.registrationContentTextView);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int correctMonth = month + 1;
                selectedDate = getDateFromCalendar(year, correctMonth, dayOfMonth);
                Log.d("MainActivity", "CalendarView date: " + selectedDate);
                Log.d("MainActivity", "Year: " + year + ", Month: " + correctMonth + ", Day: " + dayOfMonth);

                selectedTrainingMenuSet.clear(); // 違う日付を選択するたびに選択内容をクリア
                showTrainingMenuForSelectedDate(selectedDate);
            }
        });
    }

    public void showTrainingMenu(View view) {
        String[] trainingMenuItems = getResources().getStringArray(R.array.training_menu_items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("トレーニングメニュー")
                .setMultiChoiceItems(trainingMenuItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String selectedTrainingMenu = trainingMenuItems[which];
                        if (isChecked) {
                            selectedTrainingMenuSet.add(selectedTrainingMenu);
                        } else {
                            selectedTrainingMenuSet.remove(selectedTrainingMenu);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String item : selectedTrainingMenuSet) {
                            stringBuilder.append("・").append(item).append("\n");
                        }
                        String selectedItems = stringBuilder.toString();
                        registrationContentTextView.setText("トレーニング内容 \n" + "\n" + selectedItems);
                        Toast.makeText(MainActivity.this, "メニューを選択しました：", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedTrainingMenuSet.clear();
                        registrationContentTextView.setText("ここに登録内容が表示されます。");
                        Toast.makeText(MainActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveToDB(View view) {
        String date = selectedDate;
        List<String> selectedTrainingMenuList = new ArrayList<>(selectedTrainingMenuSet);

        // 既存のトレーニングメニューデータを取得
        List<String> existingTrainingMenuList = dataSource.getTrainingMenu(date);

        if (existingTrainingMenuList != null && !existingTrainingMenuList.isEmpty()) {
            // 既存のデータがある場合は更新
            existingTrainingMenuList.clear();
            existingTrainingMenuList.addAll(selectedTrainingMenuList);
            dataSource.saveTrainingMenu(date, existingTrainingMenuList);
        } else {
            // 既存のデータがない場合は新規に挿入
            dataSource.saveTrainingMenu(date, selectedTrainingMenuList);
        }

        String selectedItems = String.join(", ", selectedTrainingMenuSet);
        Toast.makeText(this, "トレーニング内容を" + "\nDBに保存しました。", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    private String getDateFromCalendar(int year, int month, int dayOfMonth) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);
    }

    private void showTrainingMenuForSelectedDate(String selectedDate) {
        List<String> trainingMenuList = dataSource.getTrainingMenu(selectedDate);
        if (trainingMenuList != null && !trainingMenuList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String item : trainingMenuList) {
                stringBuilder.append("・").append(item).append("\n");
            }
            String selectedItems = stringBuilder.toString();
            registrationContentTextView.setText("トレーニング内容 \n" + "\n" + selectedItems);
        } else {
            registrationContentTextView.setText("トレーニング内容がありません。");
        }
    }
}

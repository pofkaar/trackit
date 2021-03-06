package com.example.pplan.trackitv10;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;


import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PREFS_NAME = "MyPrefsFile";
    Button buttonDate, buttonTime, buttonTimeDayEnd, buttonAmountOfBreaksMinus, buttonAmountOfBreaksPlus;
    TextView textViewDate, textViewTime, workHoursTextView, textViewTimeDayEnd, textViewBreakDuration, textViewAmountOfBreaks;
    //reset
    int workHoursCounted = 0, workHoursCounted2 = 0, workMinutesCounted = 0, wHStart = 0, wMStart = 0, wHEnd = 0, wMEnd = 0, breakDurationCount = 0, breakAmount = 0, totalBreakTime =0;



    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonTime = (Button) findViewById(R.id.buttonTime);
        buttonTimeDayEnd = (Button) findViewById(R.id.buttonTimeDayEnd);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewTimeDayEnd = (TextView) findViewById(R.id.textViewTimeDayEnd);
        buttonDate.setOnClickListener(this);
        buttonTime.setOnClickListener(this);
        buttonTimeDayEnd.setOnClickListener(this);
        workHoursTextView = (TextView) findViewById(R.id.textViewWorkHoursCount);
        buttonAmountOfBreaksMinus = (Button) findViewById(R.id.buttonAmountOfBreaksMinus);
        buttonAmountOfBreaksPlus = (Button) findViewById(R.id.buttonAmountOfBreaksPlus);
        textViewBreakDuration = (TextView) findViewById(R.id.textViewBreakDuration);
        textViewAmountOfBreaks = (TextView) findViewById(R.id.textViewAmountOfBreaks);

        ////// GET DATA FROM STORAGE

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        workHoursCounted = settings.getInt("workHoursCounted", 0);
        workMinutesCounted = settings.getInt("workMinutesCounted", 0);
        wHStart = settings.getInt("wHStart", 0);
        wMStart =  settings.getInt("wMStart", 0);
        wHEnd = settings.getInt("wHEnd", 0);
        wMEnd = settings.getInt("wMEnd", 0);
        breakDurationCount = settings.getInt("breakDurationCount", 0);
        ////////////////////////////////
        // add rest
        updateHoursWorked();

        textViewBreakDuration.setText("Break count x "+ breakDurationCount + " min.");

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("workHoursCounted", workHoursCounted);
        editor.putInt("workMinutesCounted", workHoursCounted2);
        editor.putInt("wHStart", wHStart);
        editor.putInt("wMStart", wMStart);
        editor.putInt("wHEnd", wHEnd);
        editor.putInt("wMEnd", wMEnd);
        // add rest
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        int day, month, year, hour, minute;
        if (v == buttonDate) {
            Calendar cal = Calendar.getInstance();
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    currentDate = year + "." + (monthOfYear + 1) + "." + dayOfMonth;
                    textViewDate.setText(currentDate);
                }
            }
                    , year, month, day);
            datePickerDialog.show();
        }
        if (v == buttonTime) {
            Calendar cal = Calendar.getInstance();
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    wHStart = hourOfDay;
                    wMStart = minute;
                    textViewTime.setText(hourOfDay+":"+minute);
                }
            }, hour, minute,true);
            timePickerDialog.show();
        }
        if (v == buttonTimeDayEnd) {
            Calendar cal = Calendar.getInstance();
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    wHEnd = hourOfDay;
                    wMEnd = minute;
                    textViewTimeDayEnd.setText(hourOfDay+":"+minute);
                }
            }, hour, minute,true);
            timePickerDialog.show();
        }
    }
    public void workHoursCount (View view){
        updateHoursWorked();
        saveHistory();
    }

    private void saveHistory() {
        Map<String, String> map = loadMap();
        String workedValue = workHoursCounted + " h and " + workHoursCounted2 + " min.";
        map.put(currentDate, workedValue);
        saveMap(map);
    }

    private void updateHoursWorked() {
        totalBreakTime = 0;
        workMinutesCounted = 0;
        workHoursCounted = 0;
        workHoursCounted2 = 0;

        totalBreakTime =  breakAmount * breakDurationCount; //breakduration set in settings * amount of breaks (in minutes)
        int wHStartMin = wHStart * 60; // Start of working day in minutes.
        int wHEndMin = wHEnd * 60; // End of working day in minutes.

        workMinutesCounted = wHEndMin - wHStartMin + wMEnd - wMStart - totalBreakTime;
        workHoursCounted2 = (workMinutesCounted % 60);
        workHoursCounted = workMinutesCounted / 60 % 24;

//        workHoursCounted = wHEnd - wHStart;
//        if (workHoursCounted <= 0){
//            //workHoursCounted = Math.abs(workHoursCounted) + 12;
//            workHoursCounted = 24 + workHoursCounted;
//        }
//
//        if (totalBreakTime > 60){
//             workHoursCounted = workHoursCounted - 1;
//            totalBreakTime = 60 - totalBreakTime;
//        }else if (totalBreakTime > 120){
//            workHoursCounted = workHoursCounted - 2;
//            totalBreakTime = 120 - totalBreakTime;
//        }
//        workMinutesCounted = wMEnd - wMStart - totalBreakTime; // BREAK DURATION SUBTRACTION--------------------
//        if (workMinutesCounted < 0){
//            //workMinutesCounted = Math.abs(workMinutesCounted);
//            workMinutesCounted = 60 + workMinutesCounted;
//        }
        workHoursTextView.setText("Latest entry: " + workHoursCounted + " h " + workHoursCounted2 + " min.");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater as = getMenuInflater();
        as.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class) ;
            startActivity(intent);
        }
        if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(this, SettingsActivity.class) ;
            startActivity(intent);
        }
        if (item.getItemId() == R.id.exit) {
            AlertDialog.Builder g = new AlertDialog.Builder(this) ;
            g.setTitle("Exit") ;
            g.setMessage("Are you sure?") ;
            g.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish() ;
                }
            });
            g.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }) ;
            g.create().show() ;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveMap(Map<String,String> inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    private Map<String,String> loadMap(){
        Map<String,String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

    public void buttonMinusBreakCountPressed(View view){
        if (breakAmount > 0){
            breakAmount --;
            textViewAmountOfBreaks.setText("" + breakAmount);
        }
    }

    public void  buttonPlusBreakCountPressed(View view){
        breakAmount ++;
        textViewAmountOfBreaks.setText("" + breakAmount);
    }
}

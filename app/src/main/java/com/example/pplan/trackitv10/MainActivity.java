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
    Button buttonDate, buttonTime, buttonTimeDayEnd;
    TextView textViewDate, textViewTime, workHoursTextView, textViewTimeDayEnd;
    //reset
    int workHoursCounted = 0, workMinutesCounted = 0, wHStart = 0, wMStart = 0, wHEnd = 0, wMEnd = 0;
    private int day, month, year, hour, minute;
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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        workHoursCounted = settings.getInt("workHoursCounted", 0);
        workMinutesCounted = settings.getInt("workMinutesCounted", 0);
        wHStart = settings.getInt("wHStart", 0);
        wMStart =  settings.getInt("wMStart", 0);
        wHEnd = settings.getInt("wHEnd", 0);
        wMEnd = settings.getInt("wMEnd", 0);
        // add rest
        updateHoursWorked();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("workHoursCounted", workHoursCounted);
        editor.putInt("workMinutesCounted", workMinutesCounted);
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
        String workedValue = workHoursCounted + " h and " + workMinutesCounted + " min.";
        map.put(currentDate, workedValue);
        saveMap(map);
    }

    private void updateHoursWorked() {
        workHoursCounted = wHEnd - wHStart;
        if (workHoursCounted <= 0){
            //workHoursCounted = Math.abs(workHoursCounted) + 12;
            workHoursCounted = 24 + workHoursCounted;
        }
        workMinutesCounted = wMEnd - wMStart;
        if (workMinutesCounted < 0){
            //workMinutesCounted = Math.abs(workMinutesCounted);
            workMinutesCounted = 60 + workMinutesCounted;
        }
        workHoursTextView.setText("Today, you have worked for " + workHoursCounted + " h and " + workMinutesCounted + " min.");
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
}

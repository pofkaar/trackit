package com.example.pplan.trackitv10;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
//    public static final String PREFS_NAME = "MyPrefsFile2";
    int breakDurationCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        SharedPreferences settings2 = getSharedPreferences(PREFS_NAME, 0);
//        breakDurationCount = settings2.getInt("breakDurationCount", 0);

    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        SharedPreferences settings2 = getSharedPreferences(PREFS_NAME, 0);
//        SharedPreferences.Editor editor = settings2.edit();
//        editor.putInt("breakDurationCount", breakDurationCount);
//        // add rest
//        editor.commit();
//    }
    public void reset(View view) {
        AlertDialog.Builder warning = new AlertDialog.Builder(this);
        warning.setTitle("Delete work history?");
        warning.setMessage("Are you sure?");
        warning.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveMap(new HashMap<String, String>());
                Toast.makeText(SettingsActivity.this, "History successfully deleted.", Toast.LENGTH_SHORT).show();
            }
        });
        warning.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        warning.create().show();
    }

    public void breakDurationSet(View view){
        EditText editTextBreakDuration = (EditText) findViewById(R.id.editTextBreakDuration);
        String editTextBreakDuration2  = editTextBreakDuration.getText().toString();
        breakDurationCount = Integer.parseInt(editTextBreakDuration2);
    }

    private void saveMap(Map<String, String> inputMap) {
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }
}
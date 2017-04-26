package com.example.oiltanker.test;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

import static com.example.oiltanker.test.R.id.sample;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("HIIIILO1", "DAS SAIST RUN FOR THE CUBIE!");
        super.onCreate(savedInstanceState);
        setTheme(ToDoDataManager.getTheme());
        setContentView(R.layout.app_settings);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");

        LinearLayout colorSettings = (LinearLayout) findViewById(R.id.color_settings);

        View primary = LayoutInflater.from(this).inflate(R.layout.settings_color_item, null);
        ((TextView) primary.findViewById(R.id.color_name)).setText("Theme color");


        CircleView sample = (CircleView) primary.findViewById(R.id.sample);

        TypedValue a = new TypedValue();
        final int color;
        getTheme().resolveAttribute(android.R.attr.colorPrimary, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            color = a.data;
        } else {
            // windowBackground is not a color, probably a drawable
            color = getResources().getColor(R.color.colorPrimary);
        }

        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new ChromaDialog.Builder()
                        .initialColor(getResources().getColor(R.color.colorPrimary))
                        .colorMode(me.priyesh.chroma.ColorMode.RGB)
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(@ColorInt int i) {
                                //Resources.Theme theme = SettingsActivity.this.getTheme();
                                Window window = SettingsActivity.this.getWindow();
                                window.setNavigationBarColor(i);
                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "Select primary color");*/
                new SpectrumDialog.Builder(SettingsActivity.this.getApplicationContext())
                        .setColors(new int[] {
                                getResources().getColor(R.color.colorPrimaryGreen),
                                getResources().getColor(R.color.colorPrimaryBlue),
                                getResources().getColor(R.color.colorPrimaryBlack),
                                getResources().getColor(R.color.colorPrimaryRed),
                                getResources().getColor(R.color.colorPrimaryYellow)
                        })
                        .setSelectedColor(color)
                        .setFixedColumnCount(2)
                        .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                int[] colors = {
                                        getResources().getColor(R.color.colorPrimaryGreen),
                                        getResources().getColor(R.color.colorPrimaryBlue),
                                        getResources().getColor(R.color.colorPrimaryBlack),
                                        getResources().getColor(R.color.colorPrimaryRed),
                                        getResources().getColor(R.color.colorPrimaryYellow)
                                };
                                if (positiveResult) {
                                    if (color == colors[0]) {
                                        Log.d("COLOR:", "the new theme is green.");
                                        //SettingsActivity.this.setTheme(R.style.AppTheme_Green);
                                        ToDoDataManager.setTheme(R.style.AppTheme_Green);
                                        //ToDoDataManager.setColor(R.color.colorPrimaryGreen);
                                    } else if (color == colors[1]) {
                                        Log.d("COLOR:", "the new theme is blue.");
                                        //SettingsActivity.this.setTheme(R.style.AppTheme_Blue);
                                        ToDoDataManager.setTheme(R.style.AppTheme_Blue);
                                        //ToDoDataManager.setColor(R.color.colorPrimaryBlue);
                                    } else if (color == colors[2]) {
                                        Log.d("COLOR:", "the new theme is black.");
                                        //SettingsActivity.this.setTheme(R.style.AppTheme_Black);
                                        ToDoDataManager.setTheme(R.style.AppTheme_Black);
                                        //ToDoDataManager.setColor(R.color.colorPrimaryBlack);
                                    } else if (color == colors[3]) {
                                        Log.d("COLOR:", "the new theme is red.");
                                        //SettingsActivity.this.setTheme(R.style.AppTheme_Red);
                                        ToDoDataManager.setTheme(R.style.AppTheme_Red);
                                        //ToDoDataManager.setColor(R.color.colorPrimaryRed);
                                    } else if (color == colors[4]) {
                                        Log.d("COLOR:", "the new theme is yellow.");
                                        //SettingsActivity.this.setTheme(R.style.AppTheme_Yellow);
                                        ToDoDataManager.setTheme(R.style.AppTheme_Yellow);
                                        //ToDoDataManager.setColor(R.color.colorPrimaryYellow);
                                    }
                                    SettingsActivity.this.recreate();
                                }
                            }
                        })
                        .build()
                        .show(getSupportFragmentManager(), "Select theme color");
            }
        });
        //sample.setBackgroundColor(R.attr.colorPrimary);
        /*TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorPrimary, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            sample.setBackgroundColor(a.data);
        } else {
            // windowBackground is not a color, probably a drawable
            sample.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }*/

        Log.d("HIIIILO2", "DAS SAIST RUN FOR THE CUBIE!");
        colorSettings.addView(primary);
        //colorSettings.notify();
    }
}

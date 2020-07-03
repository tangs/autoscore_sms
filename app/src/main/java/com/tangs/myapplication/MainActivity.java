package com.tangs.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Configuration;
import android.os.Bundle;

import com.tangs.myapplication.ui.main.SettingFragment;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDarkBySys = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        boolean isDark = LocalSharedPreferences.getInstance(this).isDarkMode(isDarkBySys);
        this.setTheme(isDark ? R.style.AppTheme_Dark : R.style.AppTheme);
        setContentView(R.layout.main_activity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, new SettingFragment())
//                    .commitNow();
//        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
package com.tangs.myapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tangs.auto_score_sms.R;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDarkBySys = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        boolean isDark = LocalSharedPreferences.getInstance(this).isDarkMode(isDarkBySys);
        this.setTheme(isDark ? R.style.AppTheme_Dark : R.style.AppTheme);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!XXPermissions.hasPermission(this, Permission.RECEIVE_SMS)) {
            XXPermissions
                    .with(this)
                    .permission(Permission.RECEIVE_SMS)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            Toast.makeText(MainActivity.this,
                                    "Get permission success.",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            Toast.makeText(MainActivity.this,
                                    "Can't get permission.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
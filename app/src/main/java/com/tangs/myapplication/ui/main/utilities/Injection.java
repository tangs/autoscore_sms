package com.tangs.myapplication.ui.main.utilities;

import android.content.Context;

import com.tangs.myapplication.ui.main.data.LocalRecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;
import com.tangs.myapplication.ui.main.data.RecordDatabase;
import com.tangs.myapplication.ui.main.viewmodels.SettingViewModelFactory;

public class Injection {

    public static SettingViewModelFactory provideSettingViewModelFactory(Context context) {
        RecordDatabase database = RecordDatabase.getInstance(context);
        return new SettingViewModelFactory(new LocalRecordDataSource(database.recordDao()),
                LocalSharedPreferences.getInstance(context));
    }
}

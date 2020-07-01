package com.tangs.myapplication.ui.main;

import android.content.Context;

import com.tangs.myapplication.ui.main.data.AppDatabase;
import com.tangs.myapplication.ui.main.data.LocalUserDataSource;
import com.tangs.myapplication.ui.main.viewmodels.ViewModelFactory;

public class Injection {
    public static UserDataSource provideUserDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new LocalUserDataSource(database.userDao());
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        UserDataSource dataSource = provideUserDataSource(context);
        return new ViewModelFactory(dataSource);
    }
}

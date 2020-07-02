package com.tangs.myapplication.ui.main.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tangs.myapplication.ui.main.UserDataSource;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;

public class SettingViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource userDataSource;
    private final LocalSharedPreferences localSharedPreferences;

    public SettingViewModelFactory(UserDataSource userDataSource,
                                   LocalSharedPreferences localSharedPreferences) {
        this.userDataSource = userDataSource;
        this.localSharedPreferences = localSharedPreferences;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingViewModel.class)) {
            return (T) new SettingViewModel(userDataSource, localSharedPreferences);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}

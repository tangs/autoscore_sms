package com.tangs.myapplication.ui.main.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tangs.myapplication.ui.main.UserDataSource;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserDataSource userDataSource;

    public ViewModelFactory(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userDataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}

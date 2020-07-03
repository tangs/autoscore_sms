package com.tangs.myapplication.ui.main.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tangs.myapplication.ui.main.RecordDataSource;

public class RecordDetailViewModelFactory implements ViewModelProvider.Factory {

    private final RecordDataSource dataSource;

    public RecordDetailViewModelFactory(RecordDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordDetailViewModel.class)) {
            return (T) new RecordDetailViewModel(dataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class.");
    }
}

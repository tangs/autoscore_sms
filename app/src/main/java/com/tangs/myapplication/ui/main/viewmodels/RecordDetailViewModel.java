package com.tangs.myapplication.ui.main.viewmodels;

import androidx.lifecycle.ViewModel;

import com.tangs.myapplication.ui.main.RecordDataSource;
import com.tangs.myapplication.ui.main.data.Record;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class RecordDetailViewModel extends ViewModel {

    private final RecordDataSource dataSource;

    public RecordDetailViewModel(RecordDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Flowable<List<Record>> getRecords() {
        return dataSource.getAll();
    }

    public Completable updateRecord(Record record) {
        return dataSource.insert(record);
    }

    public Completable deleteRecord(int orderId) {
        return dataSource.deleteByOrderId(orderId);
    }

}
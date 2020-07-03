package com.tangs.myapplication.ui.main;

import com.tangs.myapplication.ui.main.data.Record;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface RecordDataSource {

    Flowable<List<Record>> getAll();

    Flowable<Record> getRecord(int orderId);

    Completable insert(Record record);

    Completable delete(Record record);

    Completable deleteAll();
}

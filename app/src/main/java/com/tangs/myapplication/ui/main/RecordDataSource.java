package com.tangs.myapplication.ui.main;

import com.tangs.myapplication.ui.main.data.Record;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

public interface RecordDataSource {

    Flowable<List<Record>> getAll();

    Maybe<Record> getRecord(int orderId);

    Completable insert(Record record);

    Completable clean();

    Completable delete(Record record);

    Completable deleteByOrderId(int orderId);

    Completable deleteAll();
}

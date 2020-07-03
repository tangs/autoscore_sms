package com.tangs.myapplication.ui.main.data;

import com.tangs.myapplication.ui.main.RecordDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class LocalRecordDataSource implements RecordDataSource {

    private final RecordDao recordDao;

    public LocalRecordDataSource(RecordDao recordDao) {
        this.recordDao = recordDao;
    }

    @Override
    public Flowable<List<Record>> getAll() {
        return recordDao.getAll();
    }

    @Override
    public Flowable<Record> getRecord(int orderId) {
        return recordDao.getRecord(orderId);
    }

    @Override
    public Completable insert(Record record) {
        return recordDao.insert(record);
    }

    @Override
    public Completable delete(Record record) {
        return recordDao.insert(record);
    }

    @Override
    public Completable deleteByOrderId(int orderId) {
        return recordDao.deleteByOrderId(orderId);
    }

    @Override
    public Completable deleteAll() {
        return recordDao.deleteAll();
    }
}

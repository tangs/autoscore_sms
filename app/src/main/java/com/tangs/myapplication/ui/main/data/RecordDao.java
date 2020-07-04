package com.tangs.myapplication.ui.main.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM (SELECT * FROM record ORDER BY date DESC LIMIT 1000) sub ORDER BY date ASC")
    Flowable<List<Record>> getAll();

    @Query("SELECT * FROM record WHERE order_id == :orderId")
    Maybe<Record> getRecord(int orderId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Record record);

    @Query("DELETE FROM record WHERE order_id == :orderId")
    Completable deleteByOrderId(int orderId);

    @Delete
    Completable delete(Record record);

    @Query("DELETE FROM record")
    Completable deleteAll();
}

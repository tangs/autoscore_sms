package com.tangs.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tangs.myapplication.ui.main.RecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalRecordDataSource;
import com.tangs.myapplication.ui.main.data.RecordDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CleanWorker extends Worker {

    private final RecordDataSource dataSource;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public CleanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        RecordDatabase database = RecordDatabase.getInstance(context);
        dataSource = new LocalRecordDataSource(database.recordDao());
    }

    @NonNull
    @Override
    public Result doWork() {
        AtomicBoolean success = new AtomicBoolean(false);
        final Object lock = new Object();
        Log.i("clean_work", "start work.");
        disposable.add(dataSource.clean()
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    success.set(true);
                    Log.i("clean_work", "work complete.");
                })
                .doOnError(err -> {
                    Log.i("clean_work", "work err:" + err.getMessage());
                })
                .doFinally(() -> {
                    synchronized (lock) {
                        lock.notify();
                    }
                    Log.i("clean_work", "work finally.");
                })
                .subscribe());
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.i("clean_work", "work ret:" + success.get());
        return success.get() ? Result.success() : Result.failure();
    }
}

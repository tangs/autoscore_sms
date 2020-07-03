package com.tangs.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tangs.myapplication.ui.main.RecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalRecordDataSource;
import com.tangs.myapplication.ui.main.data.Record;
import com.tangs.myapplication.ui.main.data.RecordDatabase;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HttpWorker extends Worker {

    // TODO test code
    private static int idx;

    private final RecordDataSource dataSource;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final Context context;
    private Record record;

    public HttpWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        RecordDatabase database = RecordDatabase.getInstance(context);
        dataSource = new LocalRecordDataSource(database.recordDao());
        Data data = workerParams.getInputData();
        record = new Record();
        record.orderId = idx++;
        record.params = data.getString("body");
        record.smsSender = data.getString("sender");
        record.smsContent = record.params;
        record.date = new Date().getTime();
    }

    @NonNull
    @Override
    public Result doWork() {
        record.state = Record.STATE_WAIT_SERVER;
        updateRecord();
        String url = record.getUrl();
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, url, future, future);
        Volley.newRequestQueue(context).add(request);
//        Result result = Result.failure();
        boolean isSuccess = false;
        try {
            future.get(10, TimeUnit.SECONDS);
            String response = future.get();
            record.state = Record.STATE_RES_OK;
            record.errMsg = "";
            dataSource.insert(record);
            isSuccess = true;
        } catch (TimeoutException e) {
            record.state = Record.STATE_TIMEOUT;
            record.errMsg = e.getMessage();
        } catch (Exception e) {
            record.state = Record.STATE_SEND_FAIL;
            record.errMsg = e.getMessage();
        }
        if (record.errMsg == null) record.errMsg = "";
        updateRecord();
        if (!isSuccess && record.canRetry()) {
            record.retry();
            return doWork();
        }
        return Result.success();
    }

    private void updateRecord() {
        disposable.add(dataSource.insert(record)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.i("sms", "end");
                }, throwable -> {
                    Log.i("sms", "fail");
                    throwable.printStackTrace();
                }));
    }
}

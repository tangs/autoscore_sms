package com.tangs.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tangs.myapplication.ui.main.RecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalRecordDataSource;
import com.tangs.myapplication.ui.main.data.LocalSharedPreferences;
import com.tangs.myapplication.ui.main.data.Record;
import com.tangs.myapplication.ui.main.data.RecordDatabase;
import com.tangs.myapplication.ui.main.data.config.Config;
import com.tangs.myapplication.ui.main.data.config.Server;
import com.tangs.myapplication.ui.main.utilities.SmsParser;
import com.tangs.myapplication.ui.main.utilities.StringHelper;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UploadService extends Service {

    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        private void updateRecord(CompositeDisposable disposable,
                                   RecordDataSource  dataSource,
                                   Record record) {
            disposable.add(dataSource.insert(record)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> Log.i("sms", "end"), throwable -> {
                        Log.i("sms", "fail");
                        throwable.printStackTrace();
                    }));
        }

        private void notifyToServer(Context context,
                                    CompositeDisposable disposable,
                                    RecordDataSource dataSource,
                                    Record record,
                                    int startId) {
            record.state = Record.STATE_WAIT_SERVER;
            updateRecord(disposable, dataSource, record);
            String url = record.getUrl();
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.GET, url, future, future);
            Volley.newRequestQueue(context).add(request);

            boolean isSuccess = false;
            try {
                future.get(10, TimeUnit.SECONDS);
                record.responseMsg = future.get();
                record.state = Record.STATE_RES_OK;
                record.errMsg = "";
                dataSource.insert(record);
                isSuccess = true;
            } catch (TimeoutException e) {
                record.responseMsg = "";
                record.state = Record.STATE_TIMEOUT;
                record.errMsg = "" + e.getMessage();
            } catch (Exception e) {
                record.responseMsg = "";
                record.state = Record.STATE_SEND_FAIL;
                record.errMsg = "" + e.getMessage();
            }
            updateRecord(disposable, dataSource, record);
            if (!isSuccess) {
                if (record.canRetry()) {
                    record.retry();
                    notifyToServer(context, disposable, dataSource, record, startId);
                    return;
                }
            }
            stopSelf(startId);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = (Context)msg.obj;
            Bundle bundle = msg.getData();
            int orderId = bundle.getInt("orderId", -1);
            LocalSharedPreferences sharedPreferences = LocalSharedPreferences.getInstance(context);
            if (orderId == -1) {
                orderId = sharedPreferences.getSmsSequenceId();
                sharedPreferences.smsSequenceIdIncrease();
            }
            String sender = bundle.getString("sender");
            String body = bundle.getString("body");

            if (StringHelper.checksNullOrEmpty(sender, body)) {
                stopSelf(msg.arg1);
                return;
            }

            CompositeDisposable disposable = new CompositeDisposable();
            RecordDatabase database = RecordDatabase.getInstance(context);
            RecordDataSource dataSource = new LocalRecordDataSource(database.recordDao());
            AtomicReference<Record> record = new AtomicReference<>();

            Map<String, String> params = SmsParser.getInstance(context).parseSms(sender, body);
            if (params == null) {
                stopSelf(msg.arg1);
                return;
            }

            try {
                String platform = sharedPreferences.getPlatform();
                if (StringHelper.checkNullOrEmpty(platform)) throw new Exception();

                Server server = Config.getInstance(context).getServer(platform);
                if (server == null) throw new Exception();

                String phone = sharedPreferences.getPhoneNumber();
                if (StringHelper.checkNullOrEmpty(phone)) throw new Exception();

                String type = params.get("type");
                if (!server.platforms.contains(type)) throw new Exception();

                params.put("terminal", phone);
                params.put("pay_id", "" + orderId);
//                params.put("url", server.url);

                int sequenceId = orderId;
                disposable.add(dataSource.getRecord(sequenceId)
                        .subscribeOn(Schedulers.io())
                        .switchIfEmpty(Single.defer(() -> Single.just(Record.Empty())))
                        .subscribe(record1 -> {
                            if (record1.isEmpty) {
                                record1 = new Record();
                                record1.orderId = sequenceId;
                                record1.date = new Date().getTime();
                                record1.lastUpdateTime = record1.date;
                                record1.state = Record.STATE_WAIT_SERVER;
                                record1.smsSender = sender;
                                record1.smsContent = body;
                                record1.host = server.url;
                                record1.params = StringHelper.convertWithIteration(params);
                            }
                            record.set(record1);
                            notifyToServer(context, disposable, dataSource, record.get(), msg.arg1);
                        }, throwable -> {
                            throwable.printStackTrace();
                            stopSelf(msg.arg1);
                        }));
            } catch (Exception e) {
                stopSelf(msg.arg1);
            }
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        String sender = intent.getStringExtra("sender");
        String body = intent.getStringExtra("body");
        int orderId = intent.getIntExtra("orderId", -1);
        if (StringHelper.checksNullOrEmpty(sender, body)) {
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        Bundle bundle = new Bundle();
        bundle.putString("sender", sender);
        bundle.putString("body", body);
        bundle.putInt("orderId", orderId);
        Message msg = serviceHandler.obtainMessage();
        msg.obj = this;
        msg.arg1 = startId;
        msg.setData(bundle);
        serviceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}

package com.tangs.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            PeriodicWorkRequest cleanWorkRequest = new PeriodicWorkRequest.Builder(
                    CleanWorker.class, 4, TimeUnit.HOURS)
                    .build();
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "clean_records1",
                    ExistingPeriodicWorkPolicy.KEEP,
                    cleanWorkRequest
            );
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus == null) return;
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    String sender = "";
                    StringBuilder bodyBuffer = new StringBuilder();
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String msgFrom = messages[i].getOriginatingAddress();
                        String msgBody = messages[i].getMessageBody();
                        if (msgFrom == null || msgBody == null) return;

                        if (sender.length() == 0) sender = msgFrom;
                        boolean isLast = i == messages.length - 1;
                        if (isLast || !sender.equals(msgFrom)) {
                            if (isLast)  bodyBuffer.append(msgBody);
                            String str = bodyBuffer.toString();
                            bodyBuffer.delete(0, bodyBuffer.length());
                            Log.i("received sms", str);

                            notifyService(context, msgFrom, str);

                            sender = msgFrom;
                            if (!isLast) bodyBuffer.append(msgBody);
                        } else {
                            bodyBuffer.append(msgBody);
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyService(Context context, String msgFrom, String body) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra("sender", msgFrom);
        intent.putExtra("body", body);
        context.startService(intent);
    }
}

package com.tangs.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    String sender = "";
                    StringBuilder bodyBuffer = new StringBuilder();
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String msgFrom = messages[i].getOriginatingAddress();
                        String msgBody = messages[i].getMessageBody();

                        if (sender.length() == 0) sender = msgFrom;
                        boolean isLast = i == messages.length - 1;
                        if (isLast || !sender.equals(msgFrom)) {
                            if (isLast)  bodyBuffer.append(msgBody);
                            String str = bodyBuffer.toString();
                            bodyBuffer.delete(0, bodyBuffer.length());
                            Log.i("received sms", str);

//                            notifyActivity(context, msgFrom, str);
                            Data data = new Data.Builder()
                                    .putString("sender", msgFrom)
                                    .putString("body", str)
                                    .build();
                            OneTimeWorkRequest request = new OneTimeWorkRequest
                                    .Builder(HttpWorker.class)
                                    .setInputData(data).build();
                            WorkManager.getInstance(context).enqueue(request);

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

    private void notifyActivity(Context context, String msgFrom, String str)
            throws PendingIntent.CanceledException {
        Intent intentCall = new Intent(context, MainActivity.class);
        intentCall.putExtra("sms", true);
        intentCall.putExtra("sender", msgFrom);
        intentCall.putExtra("body", str);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intentCall,
                PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.send();
    }
}

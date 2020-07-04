package com.tangs.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

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

                            notifyActivity(context, msgFrom, str);

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

    private void notifyActivity(Context context, String msgFrom, String body) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra("sender", msgFrom);
        intent.putExtra("body", body);
        context.startService(intent);
    }
}

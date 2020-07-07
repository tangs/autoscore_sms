package com.tangs.myapplication.ui.main.data;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tangs.myapplication.BuildConfig;
import com.tangs.myapplication.UploadService;
import com.tangs.myapplication.ui.main.utilities.StringHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "record")
public class Record {
    public static final int STATE_UNINIT = -1;
    public static final int STATE_RES_OK = 0;
    public static final int STATE_WAIT_SERVER = 1;
    public static final int STATE_TIMEOUT = 2;
    public static final int STATE_SEND_FAIL = 3;

    private static final DateFormat sdFormat = SimpleDateFormat.getDateTimeInstance();
//            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @PrimaryKey
    @ColumnInfo(name = "order_id")
    public int orderId;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "last_update_time")
    public long lastUpdateTime;

    @ColumnInfo(name = "state")
    public int state = -1;

    @ColumnInfo(name = "sms_sender")
    public String smsSender = "";

    @ColumnInfo(name = "sms_content")
    public String smsContent = "";

    @ColumnInfo(name = "host")
    public String host = "";

    @ColumnInfo(name = "params")
    public String params = "";

    @ColumnInfo(name = "response_msg")
    public String responseMsg = "";

    @ColumnInfo(name = "err_msg")
    public String errMsg = "";

    @ColumnInfo(name = "retry_time")
    public int retryTime;

    @Ignore
    public boolean isEmpty;

    public static Record Empty() {
        Record record = new Record();
        record.isEmpty = true;
        return record;
    }

    public Record() {

    }

    @Ignore
    public Record(int orderId, String host, String params) {
        this.orderId = orderId;
        this.host = host;
        this.params = params;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Record other = (Record)obj;
        return other.orderId == this.orderId
                && other.date == this.date
                && other.lastUpdateTime == this.lastUpdateTime
                && other.state == this.state
                && StringHelper.equals(other.smsSender, this.smsSender)
                && StringHelper.equals(other.smsContent, this.smsContent)
                && StringHelper.equals(other.host, this.host)
                && StringHelper.equals(other.params, this.params)
                && StringHelper.equals(other.errMsg, this.errMsg)
                && other.retryTime == this.retryTime
                && other.isEmpty == isEmpty;
    }

    public boolean isFailOrTimeout() {
        return state == STATE_TIMEOUT
                || state == STATE_SEND_FAIL
                || (state == STATE_WAIT_SERVER && new Date().getTime() - this.lastUpdateTime > 60 * 1000);
    }

    public String getStateDescribe() {
        switch (state) {
            case STATE_UNINIT: return "uninitialized";
            case STATE_RES_OK: return "OK";
            case STATE_WAIT_SERVER: return "waiting";
            case STATE_TIMEOUT: return "timeout️";
            case STATE_SEND_FAIL: return "fail️";
        }
        return "err state:" + state;
    }

    public String getStateAlert() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(state);
        switch (state) {
            case STATE_TIMEOUT:
            case STATE_SEND_FAIL:
                buffer.append("(\uD83D\uDC94)");
                break;
            case STATE_WAIT_SERVER:
                buffer.append("\uD83D\uDCE4");
                break;
        }
        return buffer.toString();
    }

    public String getDate() {
        return sdFormat.format(this.date);
    }

    public String geLastUpdateTime() {
        return sdFormat.format(this.lastUpdateTime);
    }

    public String getUrl() {
        if (BuildConfig.DEBUG) {
//            return "http://39.154.62.72:80?" + params;
            return "http://192.168.1.101:5678?" + params;
        }
        return host + "?" + params;
    }

    public boolean canRetry() {
        return retryTime < 3;
    }

    public void retry() {
        ++retryTime;
    }

    public boolean isWaiting() {
        return state == STATE_WAIT_SERVER
                && new Date().getTime() - this.lastUpdateTime < 60 * 1000;
    }

    public void upload(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra("sender", smsSender);
        intent.putExtra("body", smsContent);
        intent.putExtra("orderId", orderId);
        context.startService(intent);
    }
}

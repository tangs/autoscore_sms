package com.tangs.myapplication.ui.main.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tangs.myapplication.ui.main.utilities.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "record")
public class Record {
    public static final int STATE_UNINIT = -1;
    public static final int STATE_RES_OK = 0;
    public static final int STATE_WAIT_SERVER = 1;
    public static final int STATE_TIMEOUT = 2;
    public static final int STATE_SEND_FAIL = 3;


    private static final SimpleDateFormat sdFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @PrimaryKey
    @ColumnInfo(name = "order_id")
    public int orderId;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "state")
    public int state = -1;

    @ColumnInfo(name = "sms_sender")
    public String smsSender = "";

    @ColumnInfo(name = "sms_content")
    public String smsContent = "";

    @ColumnInfo(name = "host")
    public String host = "http://39.154.62.72:80";   // TODO test code.

    @ColumnInfo(name = "params")
    public String params = "";

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
        Record other = (Record)obj;
        if (other == null) return false;
        return other.orderId == this.orderId
                && other.date == this.date
                && other.state == this.state
                && StringHelper.equals(other.smsSender, this.smsSender)
                && StringHelper.equals(other.smsContent, this.smsContent)
                && StringHelper.equals(other.host, this.host)
                && StringHelper.equals(other.params, this.params)
                && StringHelper.equals(other.errMsg, this.errMsg)
                && other.retryTime == this.retryTime;
    }

    public boolean isFailOrTimeout() {
        return state == STATE_TIMEOUT
                || state == STATE_SEND_FAIL
                || (state == STATE_WAIT_SERVER && new Date().getTime() - this.date > 60 * 1000);
    }

    public String getStateDescribe() {
        switch (state) {
            case STATE_UNINIT: return "uninitialized";
            case STATE_RES_OK: return "OK";
            case STATE_WAIT_SERVER: return "waiting";
            case STATE_TIMEOUT: return "timeout";
            case STATE_SEND_FAIL: return "fail";
        }
        return "err state:" + state;
    }

    public String getDate() {
        return sdFormat.format(this.date);
    }

    public String getUrl() {
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
                && new Date().getTime() - this.date < 60 * 1000;
    }
}

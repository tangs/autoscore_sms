package com.tangs.myapplication.ui.main.data;

import androidx.annotation.Nullable;

public class Record {
    public int orderId;
    public long date;
    public String smsSender = "";
    public String smsContent = "";
    public String host = "";
    public String params = "";

    public Record(int orderId, String host, String params) {
        this.orderId = orderId;
        this.host = host;
        this.params = params;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Record other = (Record)obj;
        if (other == null) return false;
        return other.orderId == this.orderId;
    }
}

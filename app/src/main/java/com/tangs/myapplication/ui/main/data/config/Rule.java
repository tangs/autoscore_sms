package com.tangs.myapplication.ui.main.data.config;

public class Rule {
    public String channel;
    public String pattern;
//    public List<String> fields = new ArrayList<>();

    public boolean isValid() {
        if (channel == null || channel.length() == 0) return false;
        if (pattern == null || pattern.length() == 0) return false;
//        if (fields.size() == 0) return false;
        return true;
    }
}

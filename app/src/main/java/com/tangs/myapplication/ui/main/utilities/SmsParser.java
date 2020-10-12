package com.tangs.myapplication.ui.main.utilities;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import com.tangs.myapplication.ui.main.data.config.Config;
import com.tangs.myapplication.ui.main.data.config.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsParser {

    private static SmsParser instance;

    private List<Pair<String, Pair<Pattern, String[]>>> patterns = new ArrayList<>();

    private static List<String> getNamedGroupCandidates(String regex) {
        List<String> namedGroups = new ArrayList<>();
        Matcher m = Pattern.compile("(^|[^\\\\])\\((\\?<([a-zA-Z][a-zA-Z0-9_]*)>)?").matcher(regex);
        while (m.find()) {
            namedGroups.add(m.group(3));
        }
        return namedGroups;
    }

    private static String removeGroupName(String regex) {
        return regex.replaceAll("\\(\\?<[a-zA-Z][a-zA-Z0-9_]*>", "\\(");
    }

    private SmsParser(Context context) {
        for (Rule rule: Config.getInstance(context).rules) {
//            String[] arr = new String[rule.fields.size()];
//            arr = rule.fields.toArray(arr);
            List<String> candidates = getNamedGroupCandidates(rule.pattern);
            String[] arr = new String[candidates.size()];
            candidates.toArray(arr);
            String regex = removeGroupName(rule.pattern);
            patterns.add(new Pair<>(rule.channel, new Pair<>(
                    Pattern.compile(regex), arr
            )));
        }
    }

    public static SmsParser getInstance(Context context) {
        if (instance == null) {
            instance = new SmsParser(context);
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public Map<String, String> parseSms(String sender, String body) {
        if (sender == null || body == null) return null;
        body = body.replaceAll("\\s+", " ").trim();
        for (Pair<String, Pair<Pattern, String[]>> entry: patterns) {
            String type = entry.first;
            Pattern pattern = entry.second.first;
            String[] keys = entry.second.second;
            Matcher m = pattern.matcher(body);
            label1:
            if (m.find()) {
                Map<String, String> ret = new HashMap<>();
                for (int i = 0; i < keys.length; ++i) {
                    String key = keys[i];
                    if (key == null || key.length() == 0) continue;
                    if (m.groupCount() < i + 1) break;
                    String value = m.group(i + 1);
                    if (value == null || value.length() == 0) break label1;
                    if (key.equals("pay_money")) value = value.replace(",", "");
                    ret.put(key, value);
                }
                ret.put("type", type);
                ret.put("sms_phone_number", sender);
                return ret;
            }
        }
        return null;
    }

}

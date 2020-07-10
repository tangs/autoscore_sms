package com.tangs.myapplication.ui.main.utilities;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsParser {

    private static SmsParser instance;

    private List<Pair<String, Pair<Pattern, String[]>>> patterns = new ArrayList<>();

    private SmsParser() {
        // K-PULS
        patterns.add(new Pair<>("9001", new Pair<>(
                Pattern.compile("([0-9/]+ [0-9:]+) บช\\w+ รับโอนจาก(\\w+) ([0-9.,]+)บ( คงเหลือ[0-9.]+บ)?"),
                new String[] {
                        "bank_time",
                        "pay_card_num",
                        "pay_money",
                        "",
                }
        )));
        // K-PULS
        patterns.add(new Pair<>("9001", new Pair<>(
                Pattern.compile("([0-9/]+ [0-9:]+) บช\\w+ เงินเข้า([0-9.,]+)( คงเหลือ([0-9.,]+)บ)?"),
                new String[] {
                        "bank_time",
                        "pay_money",
                        "",
                }
        )));
        // 缅甸：WaveMoney
        patterns.add(new Pair<>("9002", new Pair<>(
                Pattern.compile("Amt: ([0-9.,]+) Ks, Sender: (\\d+), Trx ID: (\\d+)"),
                new String[] {
                        "pay_money",
                        "pay_phone",
                        "pay_order_num",
                }
        )));
        // 缅甸MPT
        patterns.add(new Pair<>("9003", new Pair<>(
                Pattern.compile("(\\d+) ထံသို ငွေ ([0-9.,]+)Ks အား လွှပေးမှု အောင်မြင်ပါသည်။ ဝန်ဆောင်ခ: [0-9.,]+Ks လုပ်ဆောင်မှုအမှတ်: (\\w+) ([0-9-]+ [0-9:]+)"),
                new String[] {
                        "pay_phone",
                        "pay_money",
                        "pay_order_num",
                        "bank_time",
                }
        )));
        // 缅甸OK-PAY
        patterns.add(new Pair<>("9004", new Pair<>(
                Pattern.compile("You have received ([0-9.,]+) MMK from ([0-9+]+) on ([0-9a-zA-Z-]+ [0-9:]+). Your Account Balance is [0-9.,]+ MMK. Transaction ID (\\w+)"),
                new String[] {
                        "pay_money",
                        "pay_phone",
                        "bank_time",
                        "pay_order_num",
                }
        )));
        // 泰国scb
        patterns.add(new Pair<>("9005", new Pair<>(
                Pattern.compile("([0-9@/:]) ([0-9.,]+) จากKBNK/x(\\d+)เข้าx\\d+"),
                new String[] {
                        "bank_time",
                        "pay_money",
                        "pay_card_num",
                }
        )));
        // 泰国scb
        patterns.add(new Pair<>("9005", new Pair<>(
                Pattern.compile("ถอน/โอนเงิน ([0-9.,]+)บ บ/ชx\\d+ ([0-9@/:])"),
                new String[] {
                        "pay_money",
                        "bank_time",
                }
        )));
        // 泰国scb
        patterns.add(new Pair<>("9005", new Pair<>(
                Pattern.compile("([0-9@/:]) ([0-9.,]+) เข้าx\\d+"),
                new String[] {
                        "bank_time",
                        "pay_money",
                }
        )));
    }

    public static SmsParser getInstance() {
        if (instance == null) {
            instance = new SmsParser();
        }
        return instance;
    }

    public Map<String, String> parseSms(String sender, String body) {
        if (sender == null || body == null) return null;
        body = body.replaceAll("\\s+", " ");
        for (Pair<String, Pair<Pattern, String[]>> entry: patterns) {
            String type = entry.first;
            Pattern pattern = entry.second.first;
            String[] keys = entry.second.second;

            Matcher m = pattern.matcher(body);
            if (m.find()) {
                Map<String, String> ret = new HashMap<>();
                for (int i = 0; i < keys.length; ++i) {
                    String key = keys[i];
                    if (key == null || key.length() == 0) continue;
                    String value = m.group(i + 1);
                    if (value == null || value.length() == 0) continue;
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

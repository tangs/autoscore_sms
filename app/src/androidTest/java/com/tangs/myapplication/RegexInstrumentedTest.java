package com.tangs.myapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tangs.myapplication.ui.main.data.config.Config;
import com.tangs.myapplication.ui.main.data.config.Rule;
import com.tangs.myapplication.ui.main.utilities.SmsParser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RegexInstrumentedTest {
    public Rule getRule(List<Rule> rules, String channel) {
        for (Rule rule: rules) {
            if (rule.channel.equals(channel)) {
                return rule;
            }
        }
        return null;
    }

    @Test
    public void regex() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        checkLocateConfig(appContext);
        checkRemoteConfig(appContext);
    }

    private void checkConfig(Context context) {
        check9021(context);
        check9025(context);
        check9026(context);
    }

    private void checkLocateConfig(Context context) {
        File file = new File(context.getFilesDir(), "config.json");
        if (file.exists()) file.delete();
        checkConfig(context);
    }

    private void checkRemoteConfig(Context context) {
        final Object lock = new Object();
        String url = "http://13.251.16.111/auto_score/config.json";
        String path = context.getFilesDir().getPath();
        FileDownloader.setup(context);
        BaseDownloadTask task = FileDownloader.getImpl().create(url);
        task.setForceReDownload(true)
                .setPath(path, true)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        assertNotNull("Download fail.", null);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                })
                .setSyncCallback(false)
                .start();
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Config.getInstance(context).refresh(context);
        SmsParser.reset();
        checkConfig(context);
    }

    private void check9021(Context context) {
        // check 9021
        SmsParser parser = SmsParser.getInstance(context);
        String smsBody = "TK 0839280970.So tien GD:+60,000.So du:19,469,460.45678901";
        Map<String, String> map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "45678901");
        assertEquals(map.get("pay_money"), "60000");
        assertEquals(map.get("type"), "9021");
        assertEquals(map.get("sms_phone_number"), "xxx");


        smsBody = "TK 0853906732.So tien GD:+50,000.So du:200,000.MBVCB.805006615.070052.23456789.CT tu 1015365176 NGUYEN THITHUY LINH toi 0853906732 LEBA HUY KY TH";
        map = parser.parseSms("yyy", smsBody);
        assertEquals(map.get("pay_order_num"), "23456789");
        assertEquals(map.get("pay_money"), "50000");
        assertEquals(map.get("type"), "9021");
        assertEquals(map.get("sms_phone_number"), "yyy");
    }

    private void check9025(Context context) {
        SmsParser parser = SmsParser.getInstance(context);
        String smsBody = "TK 232385348 tai VPB +20,000VND luc 16:28 31/01/21. So du 110,000VND. ND: NHAN TU 65610000068965 TRACE 560102 ND Chuyen tien";
        Map<String, String> map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "560102");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9025");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 232385348 tai VPB +20,000VND luc 16:39 31/01/21. So du 130,000VND. ND: NHAN TU 17041257 TRACE 792813 ND 12 310121-16:39:47 792813";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "792813");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9025");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 232385348 tai VPB +20,000VND luc 17:00 31/01/21. So du 150,000VND. ND: NHAN TU 100868894701 TRACE 275602 ND VUONG KIM MAU Chuyen tien";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "275602");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9025");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 232385348 tai VPB +50,000VND luc 17:13 31/01/21. So du 200,000VND. ND: NHAN TU 19036747692012 TRACE 443725 ND CHUYEN TIEN DEN SO TAI KHOAN 23 2385348 - CH..";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "443725");
        assertEquals(map.get("pay_money"), "50000");
        assertEquals(map.get("type"), "9025");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 232385348 tai VPB +20,000VND luc 17:21 31/01/21. So du 220,000VND. ND: NGUYEN THANH TIEN chuyen tien";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("payer_name"), "NGUYEN THANH TIEN");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9025");
        assertEquals(map.get("sms_phone_number"), "xxx");
    }

    private void check9026(Context context) {
        SmsParser parser = SmsParser.getInstance(context);
        String smsBody = "TK 4240xxxx4584|GD: +20,000VND 31/01/21 13:26|SD:270,003VND|ND: CHAU HOAI THANH chuyen tien - Ma gi ao dich/ Trace 998593 -4240167544584";
        Map<String, String> map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "998593");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 4240xxxx4584|GD: +20,000VND 31/01/21 14:20|SD:290,003VND|ND: Chuyen tien - Ma giao dich/ Trace 4 48699 -4240167544584";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "48699");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 4240xxxx4584|GD: +20,000VND 31/01/21 15:24|SD:290,003VND|ND: H 310121 15 25 12 781604 - Ma giao dich/ Trace 781604 -4240167544584";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "781604");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 4240xxxx4584|GD: +20,000VND 31/01/21 15:27|SD:310,003VND|ND: VUONG KIM MAU Chuyen tien - Ma giao dich/ Trace 245060 -4240167544584";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "245060");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 4240xxxx4584|GD: +50,000VND 31/01/21 15:46|SD:380,003VND|ND: Ck FT21032940062145 - Ma giao dich/ Trace 369426 -4240167544584";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "369426");
        assertEquals(map.get("pay_money"), "50000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 4240xxxx4584|GD: +20,000VND 31/01/21 15:34|SD:330,003VND|ND: MBVCB 969666571 088129 VUONG KIM MA U chuyen tien CT tu 1018705978 VUON G KIM MAU toi ";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "088129");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");

        smsBody = "TK 232385348 tai VPB +20,000VND luc 16:25 31/01/21. So du 90,000VND. ND: NHAN TU 4240167544584 TRACE 308437 ND Ck";
        map = parser.parseSms("xxx", smsBody);
        assertEquals(map.get("pay_order_num"), "308437");
        assertEquals(map.get("pay_money"), "20000");
        assertEquals(map.get("type"), "9026");
        assertEquals(map.get("sms_phone_number"), "xxx");
    }
}
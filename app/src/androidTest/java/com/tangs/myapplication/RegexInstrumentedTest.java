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
}
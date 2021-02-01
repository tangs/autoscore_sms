package com.tangs.myapplication;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tangs.auto_score_sms.R;
import com.tangs.myapplication.ui.main.utilities.JsonHelper;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ConfigFormatInstrumentedTest {
    @Test
    public void localConfigJsonFormatChecking() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        JSONObject obj = JsonHelper.loadJSONFromAsset(appContext, R.raw.config);
        assertNotNull(obj);
    }

    @Test
    public void remoteConfigJsonFormatChecking() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
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
            JSONObject obj = JsonHelper.loadJSONFromFiles(context, "config.json");
            assertNotNull(obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertNotNull(null);
        }
    }
}

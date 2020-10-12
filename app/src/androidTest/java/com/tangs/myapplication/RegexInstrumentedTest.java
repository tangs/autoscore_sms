package com.tangs.myapplication;

import android.content.Context;
import android.util.Pair;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.tangs.myapplication.ui.main.data.config.Config;
import com.tangs.myapplication.ui.main.data.config.Rule;
import com.tangs.myapplication.ui.main.data.config.Server;
import com.tangs.myapplication.ui.main.utilities.SmsParser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        assertEquals("com.tangs.auto_score_sms", appContext.getPackageName());
        Config config = Config.getInstance(appContext);
        Rule rule = getRule(config.rules, "9021");
        assertNotNull(rule);
//        Map<String, String>  dd = SmsParser.parseSms("xxx", "");
    }
}
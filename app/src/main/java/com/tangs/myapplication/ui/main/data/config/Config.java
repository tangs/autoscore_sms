package com.tangs.myapplication.ui.main.data.config;

import android.content.Context;

import com.tangs.auto_score_sms.R;
import com.tangs.myapplication.ui.main.utilities.JsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Config {
    public Map<String, Server> servers = new HashMap<>();
    public List<Rule> rules = new ArrayList<>();
    private static Config config;

    public static Config getInstance(Context context) {
        if (config == null) {
            config = new Config(context);
        }
        return config;
    }

    private Config(Context context) {
        loadConfig(context);
    }

    public Server getServer(String platform) {
        if (!config.servers.containsKey(platform)) return null;
        Server server = config.servers.get(platform);
        return server;
    }

    private void loadConfig(Context context) {
        String txt = JsonHelper.loadJSONFromAsset(context, R.raw.config);
        try {
            JSONObject obj = new JSONObject(txt);
            JSONObject servers = obj.getJSONObject("servers");
            this.servers.clear();
            Iterator<String> it = servers.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONObject obj1 = servers.getJSONObject(key);
                Server server = new Server();
                server.url = obj1.getString("url");
                JSONArray platforms = obj1.getJSONArray("platforms");
                for (int i = 0; i < platforms.length(); ++i) {
                    server.platforms.add(platforms.getString(i));
                }
                this.servers.put(key, server);
            }

            JSONArray rules = obj.getJSONArray("rules");
            this.rules.clear();
            for (int i = 0; i < rules.length(); ++i) {
                JSONObject ruleObj = rules.getJSONObject(i);
                Rule rule = new Rule();
                rule.channel = ruleObj.getString("channel");
                rule.pattern = ruleObj.getString("pattern");
//                JSONArray fields = ruleObj.getJSONArray("fields");
//                for (int j = 0; j < fields.length(); ++j) {
//                    rule.fields.add(fields.getString(j));
//                }
                if (rule.isValid()) {
                    this.rules.add(rule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

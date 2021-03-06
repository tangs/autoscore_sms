package com.tangs.myapplication.ui.main.data.config;

import android.content.Context;

import com.tangs.auto_score_sms.R;
import com.tangs.myapplication.BuildConfig;
import com.tangs.myapplication.ui.main.utilities.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Config {
    public Map<String, Server> servers = new HashMap<>();
    public List<Rule> rules = new ArrayList<>();
    public List<String> platforms = new ArrayList<>();
    private static Config config;

    public static Config getInstance(Context context) {
        if (config == null) {
            config = new Config(context);
        }
        return config;
    }

    private Config(Context context) {
//        loadConfig(context);
        refresh(context);
    }

    public Server getServer(String platform) {
        if (!config.servers.containsKey(platform)) return null;
        Server server = config.servers.get(platform);
        return server;
    }

    public List<String> getPlatforms() {
        List<String> list = new ArrayList();
        if (BuildConfig.IS_MIANDIAN_2_1) {
//            if (this.platforms.contains("缅甸2-1")) {
//                list.add("缅甸2-1");
//            }
            for (String platform: this.platforms) {
                if (platform.startsWith("缅甸2-")) {
                    list.add(platform);
                }
            }
        }  else if (BuildConfig.IS_MIANDIAN_3_1) {
//            if (this.platforms.contains("缅甸3-1")) {
//                list.add("缅甸3-1");
//            }
            for (String platform: this.platforms) {
                if (platform.startsWith("缅甸3-")) {
                    list.add(platform);
                }
            }
        } else {
            for (String platform: this.platforms) {
                if (!platform.startsWith("缅甸2-") && !platform.startsWith("缅甸3-")) {
                    list.add(platform);
                }
            }
        }
        return list;
    }

    private boolean loadConfig(JSONObject obj) {
        try {
            this.servers.clear();
            this.rules.clear();
            this.platforms.clear();

            addServers(obj.getJSONObject("servers"));
            addServers(obj.getJSONObject("serversNew"));

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
            return false;
        }
        return true;
    }

    private void addServers(JSONObject servers) throws JSONException {
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
            this.platforms.add(key);
        }
    }

    public void refresh(Context context) {
        if (!loadConfig(JsonHelper.loadJSONFromFiles(context, "config.json"))) {
            loadConfig(JsonHelper.loadJSONFromAsset(context, R.raw.config));
        }
    }

}

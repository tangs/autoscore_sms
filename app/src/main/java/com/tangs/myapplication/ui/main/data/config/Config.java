package com.tangs.myapplication.ui.main.data.config;

import android.content.Context;
import android.util.JsonReader;

import com.tangs.myapplication.R;
import com.tangs.myapplication.ui.main.utilities.JsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Config {
    public Map<String, Server> servers = new HashMap<>();
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

    public Server geServer(String platform) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

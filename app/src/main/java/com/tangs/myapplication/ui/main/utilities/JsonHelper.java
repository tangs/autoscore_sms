package com.tangs.myapplication.ui.main.utilities;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonHelper {
    public static String loadJSONFromAsset(Context context, int id) {
        String json;
        try {
            InputStream is = context.getResources().openRawResource(id);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

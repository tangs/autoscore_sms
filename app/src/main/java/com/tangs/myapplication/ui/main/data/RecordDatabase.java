package com.tangs.myapplication.ui.main.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 1, exportSchema = false)
public abstract class RecordDatabase extends RoomDatabase {
    private static RecordDatabase instance;

    public abstract RecordDao recordDao();

    public static RecordDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RecordDatabase.class, "record.db").build();
        }
        return instance;
    }
}

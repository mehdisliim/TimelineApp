package com.example.timelineapp.DataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {XP.class}, version = 1)
public abstract class XPDatabase extends RoomDatabase {
    private static XPDatabase instance;

    public abstract XPDao xpDao();

    public static synchronized XPDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            XPDatabase.class, "xp_database")
                    .allowMainThreadQueries() // not doing any heavy db queries so this should be fine
                    .build();
        }
        return instance;
    }
}
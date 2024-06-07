package com.example.timelineapp.DataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase instance;

    public abstract UserDao UserDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, "xp_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // not doing any heavy db queries so this should be fine
                    .build();
        }
        return instance;
    }
}
package com.example.timelineapp.DataBase;

import static com.example.timelineapp.GameViewModel.USER_ID;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import java.util.List;

@Dao
public interface XPDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(XP xp);

    @Query("SELECT * FROM xp_table WHERE id = :userId LIMIT 1")
    XP getUserXP(int userId);

    @Query("UPDATE xp_table SET experiencePoints = :xp WHERE id = :userId")
    void updateUserXP(int userId, int xp);

}
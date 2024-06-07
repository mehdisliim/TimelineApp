package com.example.timelineapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE id = :userId LIMIT 1")
    User getUser(int userId);

    @Query("UPDATE user_table SET experiencePoints = :xp WHERE id = :userId")
    void updateUserXP(int userId, int xp);


    @Insert
    void insertUser(User user);


}
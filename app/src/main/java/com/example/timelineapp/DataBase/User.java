package com.example.timelineapp.DataBase;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.timelineapp.GameViewModel;

@Entity(tableName = "user_table")
public class User {
        @PrimaryKey
        public int id;

        @ColumnInfo(name = "experiencePoints")
        public int experiencePoints;

        @ColumnInfo(name = "fullName")
        public String fullName;

        @ColumnInfo(name = "username")
        public String username;

        @ColumnInfo(name = "email")
        public String email;

        @ColumnInfo(name = "foundAppOn")
        public String foundAppOn;

        public User(int id, int experiencePoints, String fullName, String username, String email, String foundAppOn) {
                this.id = id;
                this.experiencePoints = experiencePoints;
                this.fullName = fullName;
                this.username = username;
                this.email = email;
                this.foundAppOn = foundAppOn;
        }



        public int getExperiencePoints() {
                return experiencePoints;
        }

        public void setExperiencePoints(int experiencePoints) {
                this.experiencePoints = experiencePoints;
        }
}
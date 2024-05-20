package com.example.timelineapp.DataBase;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.timelineapp.GameViewModel;

@Entity(tableName = "xp_table")
public class XP {
        @PrimaryKey
        public int id = GameViewModel.USER_ID;

        @ColumnInfo(name = "experiencePoints")
        public int experiencePoints;

        public XP(int experiencePoints) {
                this.experiencePoints = experiencePoints;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getExperiencePoints() {
                return experiencePoints;
        }

        public void setExperiencePoints(int experiencePoints) {
                this.experiencePoints = experiencePoints;
        }
}
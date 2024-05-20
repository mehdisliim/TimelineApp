package com.example.timelineapp;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


public class GameViewModel extends ViewModel {
    public final static Integer USER_ID = 123;
    Timeline chosenTimeline;
    ArrayList<Integer> correctAnsweredEvents = new ArrayList<Integer>();

    public void setChosenTimeline(Timeline chosenTimeline) {
        this.chosenTimeline = chosenTimeline;
    }

    public Timeline getChosenTimeline() {
        return chosenTimeline;

    }

    public void addCorrectAnsweredEventInList(Integer eventIndex) {
        if (!correctAnsweredEvents.contains(eventIndex)){
            correctAnsweredEvents.add(eventIndex);
        }
    }


    public boolean isEventAlreadyAnsweredCorrectly(Integer eventIndex) {
        return correctAnsweredEvents.contains(eventIndex);
    }

    private Integer xp = 0;

    private Integer  hearts = 3;

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getHearts() {
        return hearts;
    }

    public void setHearts(Integer hearts) {
        this.hearts = hearts;
    }
}
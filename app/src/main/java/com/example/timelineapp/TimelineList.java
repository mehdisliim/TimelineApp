package com.example.timelineapp;

import java.util.ArrayList;

public class TimelineList {
    private ArrayList<Timeline> timelines;

    public TimelineList(ArrayList<Timeline> timelines) {
        this.timelines = timelines;
    }

    public ArrayList<Timeline> getTimelines() {
        return timelines;
    }

    public void setTimelines(ArrayList<Timeline> timelines) {
        this.timelines = timelines;
    }
}
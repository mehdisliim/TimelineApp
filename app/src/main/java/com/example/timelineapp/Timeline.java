package com.example.timelineapp;

import java.util.List;

public class Timeline {
    private String title;
    private List<Integer> timelinePoints;
    private List<String> timeLineCorrectAnswers;

    // Constructor, getters, and setters
    public Timeline(String title, List<Integer> timelinePoints, List<String> timeLineCorrectAnswers) {
        this.title = title;
        this.timelinePoints = timelinePoints;
        this.timeLineCorrectAnswers = timeLineCorrectAnswers;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getTimelinePoints() {
        return timelinePoints;
    }

    public void setTimelinePoints(List<Integer> timelinePoints) {
        this.timelinePoints = timelinePoints;
    }

    public List<String> getTimeLineCorrectAnswers() {
        return timeLineCorrectAnswers;
    }

    public void setTimeLineCorrectAnswers(List<String> timeLineCorrectAnswers) {
        this.timeLineCorrectAnswers = timeLineCorrectAnswers;
    }
}
package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Workout implements Serializable {
    private final String day;
    private final String workoutName;
    private final String intensity;
    private final int duration;
    private final List<String> exercises;

    public Workout(String day, String workoutName, String intensity, int duration, List<String> exercises) {
        this.day = day;
        this.workoutName = workoutName;
        this.intensity = intensity;
        this.duration = duration;
        this.exercises = exercises == null ? new ArrayList<>() : exercises;
    }

    public String getDay() {
        return day;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getIntensity() {
        return intensity;
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public String getExercisesPreview() {
        if (exercises.isEmpty()) return "No exercises listed";
        int count = Math.min(exercises.size(), 3);
        return String.join(", ", exercises.subList(0, count));
    }
}
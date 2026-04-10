package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    private final OnWorkoutClickListener listener;
    private List<Workout> workouts = new ArrayList<>();

    public WorkoutsAdapter(OnWorkoutClickListener listener) {
        this.listener = listener;
    }

    public void setWorkouts(List<Workout> newList) {
        workouts = newList == null ? new ArrayList<>() : newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workouts.get(position);

        holder.tvWorkoutName.setText(capitalize(workout.getDay()) + ", " + workout.getWorkoutName());
        holder.tvWorkoutDuration.setText(workout.getDuration() + " mins");
        holder.tvWorkoutDifficulty.setText(capitalize(workout.getIntensity()));
        holder.tvWorkoutExercises.setText(workout.getExercisesPreview());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onWorkoutClick(workout);
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutName, tvWorkoutDuration, tvWorkoutDifficulty, tvWorkoutExercises;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvWorkoutDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvWorkoutDifficulty = itemView.findViewById(R.id.tvWorkoutDifficulty);
            tvWorkoutExercises = itemView.findViewById(R.id.tvWorkoutExercises);
        }
    }
}
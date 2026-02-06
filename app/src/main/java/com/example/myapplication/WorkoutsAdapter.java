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
        if (newList == null) {
            workouts = new ArrayList<>();
        } else {
            workouts = newList;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout, listener);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // TextViews
        TextView tvName;
        TextView tvDuration;
        TextView tvDifficulty;
        TextView tvExercises;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWorkoutName);
            tvDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvDifficulty = itemView.findViewById(R.id.tvWorkoutDifficulty);
            tvExercises = itemView.findViewById(R.id.tvWorkoutExercises);
        }

        void bind(final Workout workout, final OnWorkoutClickListener listener) {


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWorkoutClick(workout);
                }
            });
        }
    }
}

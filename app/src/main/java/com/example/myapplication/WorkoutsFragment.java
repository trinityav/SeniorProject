package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsFragment extends Fragment {

    private View todayView;
    private View allView;

    private WorkoutsAdapter adapter;

    private List<Workout> allWorkouts = new ArrayList<>();
    private Workout todayWorkout;

    public WorkoutsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_workouts, container, false);

        initWorkouts();

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        FrameLayout tabContent = root.findViewById(R.id.tabContent);

        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("All Workouts"));

        todayView = inflater.inflate(R.layout.today_workout_content, tabContent, false);
        allView = inflater.inflate(R.layout.all_workouts_content, tabContent, false);

        tabContent.addView(todayView);
        tabContent.addView(allView);
        allView.setVisibility(View.GONE);

        setupTodayView(todayView);
        setupAllView(allView);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    todayView.setVisibility(View.VISIBLE);
                    allView.setVisibility(View.GONE);
                } else {
                    todayView.setVisibility(View.GONE);
                    allView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return root;
    }

    private void initWorkouts() {
        allWorkouts.add(new Workout(1));
        allWorkouts.add(new Workout(2));
        allWorkouts.add(new Workout(3));
        allWorkouts.add(new Workout(4));
        allWorkouts.add(new Workout(5));
        allWorkouts.add(new Workout(6));


        todayWorkout = allWorkouts.get(0);
    }

    private void setupTodayView(View view) {
        Button btnStart = view.findViewById(R.id.btnStartWorkout);
        btnStart.setOnClickListener(v -> openWorkoutDetails(todayWorkout.getId()));
    }

    private void setupAllView(View view) {
        RecyclerView rv = view.findViewById(R.id.rvWorkouts);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new WorkoutsAdapter(workout ->
                openWorkoutDetails(workout.getId()));

        rv.setAdapter(adapter);
        adapter.setWorkouts(allWorkouts);
    }

    private void openWorkoutDetails(int workoutId) {
        Intent intent = new Intent(requireContext(), WorkoutDetailsActivity.class);
        intent.putExtra("workout_id", workoutId);
        startActivity(intent);
    }
}

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class HomeScreen extends BaseActivity {

    private static final String[] GREETINGS = {
            "Hi Student,\nLets check Your Activity!",
            "Hi Student,\nYou're crushing it today! 💪",
            "Hi Student,\nEvery rep counts. Let's go!",
            "Hi Student,\nChampions train every day!",
            "Hi Student,\nYour future self will thank you!",
            "Hi Student,\nNo days off. Let's move! 🔥",
            "Hi Student,\nConsistency is the key. Let's train!",
            "Hi Student,\nPain today, strength tomorrow! 💥"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setActivityLayout(R.layout.activity_home_screen); // ← your layout file name

        // Session check
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Randomized greeting
        TextView greetingText = findViewById(R.id.greetingText);
        if (greetingText != null) {
            greetingText.setText(GREETINGS[new Random().nextInt(GREETINGS.length)]);
        }

        // Upcoming workout date — defaults to tomorrow
        TextView tvUpcomingDate = findViewById(R.id.tvUpcomingDate);
        if (tvUpcomingDate != null) {
            Calendar next = Calendar.getInstance();
            next.add(Calendar.DAY_OF_YEAR, 1);
            String date = new SimpleDateFormat("EEEE, MMM d yyyy", Locale.getDefault()).format(next.getTime());
            tvUpcomingDate.setText(date);
        }

        TextView tvUpcomingWorkoutName = findViewById(R.id.tvUpcomingWorkoutName);
        if (tvUpcomingWorkoutName != null) {
            tvUpcomingWorkoutName.setText("Tap to schedule a workout");
        }

        // Total workouts — placeholder until Firebase is set up
        TextView tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        if (tvTotalWorkouts != null) {
            tvTotalWorkouts.setText("0");
        }

        // Start Workout button
        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);
        if (startWorkoutButton != null) {
            startWorkoutButton.setOnClickListener(v ->
                    startActivity(new Intent(HomeScreen.this, WorkoutsActivity.class))
            );
        }

        // AI Chat card — skeleton for now
        CardView aiChatCard = findViewById(R.id.aiChatCard);
        // TODO: startActivity(new Intent(HomeScreen.this, ChatActivity.class));

        // Upcoming workout card — skeleton for now
        CardView upcomingWorkoutCard = findViewById(R.id.upcomingWorkoutCard);
        // TODO: startActivity(new Intent(HomeScreen.this, StatsActivity.class));
    }
}
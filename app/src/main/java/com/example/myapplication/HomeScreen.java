package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeScreen extends BaseActivity {

    private CardView cardAiCoach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_home_screen);

        cardAiCoach = findViewById(R.id.cardAiCoach);

        cardAiCoach.setOnClickListener(v -> showAiCoachDialog());
    }

    private void showAiCoachDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ai_coach, null);
        builder.setView(dialogView);

        EditText etUserQuestion = dialogView.findViewById(R.id.etUserQuestion);
        TextView tvAiResponse = dialogView.findViewById(R.id.tvAiResponse);
        Button btnSend = dialogView.findViewById(R.id.btnSend);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSend.setOnClickListener(v -> {
            String userQuestion = etUserQuestion.getText().toString().trim();

            if (userQuestion.isEmpty()) {
                Toast.makeText(this, "Please type a question", Toast.LENGTH_SHORT).show();
                return;
            }

            String response = getAiCoachResponse(userQuestion);
            tvAiResponse.setText(response);
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private String getAiCoachResponse(String question) {
        question = question.toLowerCase();

        if (question.contains("leg")) {
            return "For leg day, try squats, lunges, and leg press. Start with light weight and focus on good form.";
        } else if (question.contains("chest")) {
            return "For chest workout, you can do bench press, push-ups, and dumbbell fly.";
        } else if (question.contains("cardio")) {
            return "For cardio, try walking, jogging, cycling, or jump rope for 20 to 30 minutes.";
        } else if (question.contains("weight loss")) {
            return "For weight loss, stay active, eat balanced meals, and stay consistent with cardio and strength workouts.";
        } else if (question.contains("abs")) {
            return "For abs, try crunches, leg raises, planks, and mountain climbers.";
        } else {
            return "That is a good question. Try staying consistent, using proper form, and choosing workouts based on your fitness goal.";
        }
    }
}
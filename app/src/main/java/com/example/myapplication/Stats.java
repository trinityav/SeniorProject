package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class Stats extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.activity_stats, findViewById(R.id.container), true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });


    }
}
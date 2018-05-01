package com.dican732.cosc345app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    VoiceRecognition voiceRecognition; // TODO: Make it so that the voice recogniser isn't reinitialised every time the user navigates to this activity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceRecognition = new VoiceRecognition(this);
        setupMenuButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        voiceRecognition.close();
    }

    private void setupMenuButtons() {
        setupMenuButton(R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(R.id.rhythmsMenuBtn, RhythmsMenu.class);
    }

    private void setupMenuButton(int btnResourceId, final Class<?> activityToOpen) {
        Button btn = findViewById(btnResourceId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Add slide left animations for when switching between activities. */
                startActivity(new Intent(MainActivity.this, activityToOpen));
            }
        });
    }
}
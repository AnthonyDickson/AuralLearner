package com.dican732.cosc345app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    VoiceRecognitionManager voiceRecognitionManager; // TODO: Make it so that the voice recogniser isn't reinitialised every time the user navigates to this activity.
    TextToSpeechManager textToSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceRecognitionManager = new VoiceRecognitionManager(this);
        voiceRecognitionManager.registerAction(new MenuAction("greeting", () -> textToSpeechManager.speak("Hello")));
        voiceRecognitionManager.registerAction(new MenuAction("count", () -> textToSpeechManager.speak("1 2 3 4 5 6 7 8 9 10")));
        textToSpeechManager = new TextToSpeechManager(this);
        setupMenuButtons();
    }

    @Override
    protected void onPause() {
        voiceRecognitionManager.close();
        textToSpeechManager.close();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        voiceRecognitionManager.close();
        textToSpeechManager.close();

        super.onDestroy();
    }

    private void setupMenuButtons() {
        setupMenuButton(R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(R.id.rhythmsMenuBtn, RhythmsMenu.class);
    }

    private void setupMenuButton(int btnResourceId, final Class<?> activityToOpen) {
        Button btn = findViewById(btnResourceId);
        btn.setOnClickListener(v -> {
            /* TODO: Add slide left animations for when switching between activities. */
            startActivity(new Intent(MainActivity.this, activityToOpen));
        });
    }
}

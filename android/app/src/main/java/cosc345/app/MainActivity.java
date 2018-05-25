package cosc345.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import cosc345.app.lib.MenuAction;
import cosc345.app.lib.TextToSpeechManager;
import cosc345.app.lib.VoiceRecognitionManager;
import cosc345.app.views.IntervalsMenu;
import cosc345.app.views.MelodiesMenu;
import cosc345.app.views.RhythmsMenu;
import cosc345.app.views.fftTest;

public class MainActivity extends AppCompatActivity {
    VoiceRecognitionManager voiceRecognitionManager;
    TextToSpeechManager textToSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cosc345.app.R.layout.activity_main);

        setupVoiceRecognition();
        setupTextToSpeech();
        setupMenuButtons();
    }

    private void setupTextToSpeech() {
        textToSpeechManager = new TextToSpeechManager(this, () -> voiceRecognitionManager.pause(),
                () -> voiceRecognitionManager.resume());
    }

    private void setupVoiceRecognition() {
        if (voiceRecognitionManager != null) {
            voiceRecognitionManager.resume();
            return;
        }

        voiceRecognitionManager = VoiceRecognitionManager.getInstance(this);
        voiceRecognitionManager.registerAction(new MenuAction("intervals", () -> startActivity(new Intent(MainActivity.this, IntervalsMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("melodies", () -> startActivity(new Intent(MainActivity.this, MelodiesMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("rhythms", () -> startActivity(new Intent(MainActivity.this, RhythmsMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("test", () -> startActivity(new Intent(MainActivity.this, fftTest.class))));
        voiceRecognitionManager.registerAction(new MenuAction("help", () -> {
            String text = getResources().getString(R.string.menuHelpText);
            textToSpeechManager.speak(text);
        }));
    }

    private void setupMenuButtons() {
        setupMenuButton(cosc345.app.R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(cosc345.app.R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(cosc345.app.R.id.rhythmsMenuBtn, RhythmsMenu.class);
        setupMenuButton(cosc345.app.R.id.fftTestBtn, fftTest.class);
    }

    private void setupMenuButton(int btnResourceId, final Class<?> activityToOpen) {
        Button btn = findViewById(btnResourceId);
        btn.setOnClickListener(v -> {
            /* TODO: Add slide left animations for when switching between activities. */
            startActivity(new Intent(MainActivity.this, activityToOpen));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MainActivity", "Resumed.");
        voiceRecognitionManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        voiceRecognitionManager.close();
        textToSpeechManager.close();
    }
}

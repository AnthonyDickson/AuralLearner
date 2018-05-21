package cosc345.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import cosc345.app.lib.MenuAction;
import cosc345.app.lib.TextToSpeechManager;
import cosc345.app.lib.VoiceRecognitionManager;
import cosc345.app.views.fftTest;
import cosc345.app.views.IntervalsMenu;
import cosc345.app.views.MelodiesMenu;
import cosc345.app.views.RhythmsMenu;

public class MainActivity extends AppCompatActivity {
    VoiceRecognitionManager voiceRecognitionManager; // TODO: Make it so that the voice recogniser isn't reinitialised every time the user navigates to this activity.
    TextToSpeechManager textToSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cosc345.app.R.layout.activity_main);
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
}

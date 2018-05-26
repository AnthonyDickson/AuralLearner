package cosc345.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cosc345.app.lib.MenuAction;
import cosc345.app.lib.TextToSpeechManager;
import cosc345.app.lib.VoiceRecognitionManager;
import cosc345.app.views.IntervalsMenu;
import cosc345.app.views.MelodiesMenu;
import cosc345.app.views.RhythmsMenu;
import cosc345.app.views.VoiceControlActivity;
import cosc345.app.views.fftTest;

/**
 * The main entry point for the application.
 */
public class MainActivity extends VoiceControlActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cosc345.app.R.layout.activity_main);

        setupVoiceRecognition();
        setupTextToSpeech();
        setupMenuButtons();
    }

    private void setupTextToSpeech() {
        TextToSpeechManager.getInstance().init(
                this,
                () -> VoiceRecognitionManager.getInstance().pause(),
                () -> VoiceRecognitionManager.getInstance().resume());
    }

    private void setupVoiceRecognition() {
        VoiceRecognitionManager voiceRecognitionManager = VoiceRecognitionManager.getInstance();
        voiceRecognitionManager.init(this);
        voiceRecognitionManager.registerAction(new MenuAction("intervals", () -> startActivity(new Intent(MainActivity.this, IntervalsMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("melodies", () -> startActivity(new Intent(MainActivity.this, MelodiesMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("rhythms", () -> startActivity(new Intent(MainActivity.this, RhythmsMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("test", () -> startActivity(new Intent(MainActivity.this, fftTest.class))));
        voiceRecognitionManager.registerAction(new MenuAction("help", () -> {
            String text = getResources().getString(R.string.menuHelpText);
            TextToSpeechManager.getInstance().speak(text);
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
    protected void onDestroy() {
        super.onDestroy();

        VoiceRecognitionManager.getInstance().close();
        TextToSpeechManager.getInstance().close();
    }
}

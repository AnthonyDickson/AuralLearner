package cosc345.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import cosc345.app.lib.MenuAction;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;
import cosc345.app.view.IntervalsMenu;
import cosc345.app.view.MelodiesMenu;
import cosc345.app.view.PitchMatching;
import cosc345.app.view.RhythmsMenu;
import cosc345.app.view.VoiceControlActivity;
import cosc345.app.view.testing.PlayNote;
import cosc345.app.view.testing.fftTest;

/**
 * The main entry point for the application.
 */
public class MainActivity extends VoiceControlActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int PERMISSIONS_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int recordAudioPermissions = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int writePermissions = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (recordAudioPermissions == PackageManager.PERMISSION_GRANTED &&
                writePermissions == PackageManager.PERMISSION_GRANTED) {
            setupVoiceRecognition();
            setupTextToSpeech();
        } else {
            String[] permissionsToRequest = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            ActivityCompat.requestPermissions(this, permissionsToRequest,
                    MainActivity.PERMISSIONS_REQUEST_CODE);
        }

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
        voiceRecognitionManager.registerAction(new MenuAction("note player", () -> startActivity(new Intent(MainActivity.this, PlayNote.class))));
        voiceRecognitionManager.registerAction(new MenuAction("pitch matching", () -> startActivity(new Intent(MainActivity.this, PitchMatching.class))));
        voiceRecognitionManager.registerAction(new MenuAction("help", () -> {
            String text = getResources().getString(R.string.menuHelpText);
            TextToSpeechManager.getInstance().speak(text);
        }));
        voiceRecognitionManager.registerAction(new MenuAction("cancel", null));
    }

    private void setupMenuButtons() {
        setupMenuButton(cosc345.app.R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(cosc345.app.R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(cosc345.app.R.id.rhythmsMenuBtn, RhythmsMenu.class);
        setupMenuButton(cosc345.app.R.id.fftTestBtn, fftTest.class);
        setupMenuButton(R.id.playNoteMenuBtn, PlayNote.class);
        setupMenuButton(R.id.pitchMatchingMenuBtn, PitchMatching.class);
    }

    private void setupMenuButton(int btnResourceId, Class<?> activityToOpen) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupVoiceRecognition();
                setupTextToSpeech();
            }
        }
    }
}

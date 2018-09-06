package cosc345.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import cosc345.app.model.MenuAction;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;
import cosc345.app.controller.IntervalsExercise;
import cosc345.app.controller.MelodiesMenu;
import cosc345.app.controller.PitchMatchingExercise;
import cosc345.app.controller.RhythmsMenu;
import cosc345.app.controller.VoiceControlActivity;

/**
 * The main entry point for the application.
 */
public class MainActivity extends VoiceControlActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    // TODO: add help screen/menu.
    public static final int PERMISSIONS_REQUEST_CODE = 101;

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
            AlertDialog alertDialog = createPermissionsExplanationDialog(((dialog, which) -> {
                String[] permissionsToRequest = {
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                ActivityCompat.requestPermissions(this, permissionsToRequest,
                        MainActivity.PERMISSIONS_REQUEST_CODE);
            }));

            alertDialog.show();
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
        voiceRecognitionManager.registerAction(new MenuAction("pitch matching", () -> startActivity(new Intent(MainActivity.this, PitchMatchingExercise.class))));
        voiceRecognitionManager.registerAction(new MenuAction("intervals", () -> startActivity(new Intent(MainActivity.this, IntervalsExercise.class))));
        voiceRecognitionManager.registerAction(new MenuAction("melodies", () -> startActivity(new Intent(MainActivity.this, MelodiesMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("rhythms", () -> startActivity(new Intent(MainActivity.this, RhythmsMenu.class))));
        voiceRecognitionManager.registerAction(new MenuAction("help", () -> {
            String text = getResources().getString(R.string.voiceControlHelp);
            TextToSpeechManager.getInstance().speak(text);
        }));
        voiceRecognitionManager.registerAction(new MenuAction("cancel", null));
    }

    private void setupMenuButtons() {
        setupMenuButton(R.id.pitchMatchingMenuBtn, PitchMatchingExercise.class);
        setupMenuButton(R.id.intervalsMenuBtn, IntervalsExercise.class);
        setupMenuButton(R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(R.id.rhythmsMenuBtn, RhythmsMenu.class);
        findViewById(R.id.voiceControlHelpBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.voiceControlHelpTitle)
                    .setMessage(R.string.voiceControlHelp)
                    .setPositiveButton(R.string.dialogOk, null);
            builder.create()
                    .show();
        });
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

    private AlertDialog createPermissionsExplanationDialog(DialogInterface.OnClickListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permissionsExplanationTitle)
                .setMessage(R.string.permissionsExplanation)
                .setPositiveButton(R.string.dialogOk, callback);
        return builder.create();
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

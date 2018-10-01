package cosc345.AuralLearner;

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

import cosc345.AuralLearner.controller.IntervalsExercise;
import cosc345.AuralLearner.controller.IntervalsMenu;
import cosc345.AuralLearner.controller.MelodiesExercise;
import cosc345.AuralLearner.controller.ScalesExercise;
import cosc345.AuralLearner.model.Difficulty;
import cosc345.AuralLearner.model.MenuAction;
import cosc345.AuralLearner.model.TextToSpeechManager;
import cosc345.AuralLearner.model.VoiceRecognitionManager;
import cosc345.AuralLearner.controller.MelodiesMenu;
import cosc345.AuralLearner.controller.PitchMatchingExercise;
import cosc345.AuralLearner.controller.VoiceControlActivity;

/**
 * The main entry point for the application.
 */
public class MainActivity extends VoiceControlActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    // TODO: add help screen/menu.
    public static final int PERMISSIONS_REQUEST_CODE = 101;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cosc345.AuralLearner.R.layout.activity_main);

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
                this, null, null);
//                () -> VoiceRecognitionManager.getInstance().pause(),
//                () -> VoiceRecognitionManager.getInstance().resume());
    }

    private void setupVoiceRecognition() {
        VoiceRecognitionManager voiceRecognitionManager = VoiceRecognitionManager.getInstance();
        voiceRecognitionManager.init(this);
        voiceRecognitionManager.registerAction(
                new MenuAction("pitch matching", () -> {
                    startActivity(new Intent(MainActivity.this, PitchMatchingExercise.class));
                }));

        setupIntervalsCommands(voiceRecognitionManager);
        setupMelodiesCommands(voiceRecognitionManager);

        voiceRecognitionManager.registerAction(new MenuAction("scales", () -> {
            startActivity(new Intent(MainActivity.this, ScalesExercise.class));
        }));
        voiceRecognitionManager.registerAction(new MenuAction("terminology", () -> {
            String text = getResources().getString(cosc345.AuralLearner.R.string.terminologyText);
            TextToSpeechManager.getInstance().speak(text);
        }));
        voiceRecognitionManager.registerAction(new MenuAction("about", () -> {
            String text = getResources().getString(cosc345.AuralLearner.R.string.aboutText);
            TextToSpeechManager.getInstance().speak(text);
        }));
        voiceRecognitionManager.registerAction(new MenuAction("help", () -> {
            String text = getResources().getString(cosc345.AuralLearner.R.string.voiceControlHelp);
            TextToSpeechManager.getInstance().speak(text);
        }));

        voiceRecognitionManager.registerAction(new MenuAction("stop speaking", () -> {
            TextToSpeechManager.getInstance().pause();
        }));
        voiceRecognitionManager.registerAction(new MenuAction("stop talking", () -> {
            TextToSpeechManager.getInstance().pause();
        }));

        voiceRecognitionManager.registerAction(new MenuAction("cancel", null));
    }

    private void setupIntervalsCommands(VoiceRecognitionManager voiceRecognitionManager) {
        voiceRecognitionManager.registerAction(
                new MenuAction("intervals", () -> {
                    startActivity(new Intent(MainActivity.this, IntervalsMenu.class));
                }));

        voiceRecognitionManager.registerAction(
                new MenuAction("intervals help", () -> {
                    String text = getResources().getString(cosc345.AuralLearner.R.string.intervalsMenu_difficultyHelpText);
                    TextToSpeechManager.getInstance().speak(text);
                }));

        for (Difficulty d: Difficulty.values()) {
            voiceRecognitionManager.registerAction(
                    new MenuAction("intervals " + d.toString().toLowerCase(), () -> {
                        Intent intent = new Intent(MainActivity.this, IntervalsExercise.class);
                        intent.putExtra("EXTRA_DIFFICULTY", d.toString());
                        startActivity(intent);
                    }));

            voiceRecognitionManager.registerAction(
                    new MenuAction("intervals " + d.toString().toLowerCase() + " help", () -> {
                        String text = getResources().getString(cosc345.AuralLearner.R.string.intervalsMenu_difficultyHelpText);
                        TextToSpeechManager.getInstance().speak(text);
                    }));
        }
    }
    private void setupMelodiesCommands(VoiceRecognitionManager voiceRecognitionManager) {
        voiceRecognitionManager.registerAction(
                new MenuAction("melodies", () -> {
                    startActivity(new Intent(MainActivity.this, MelodiesMenu.class));
                }));

        voiceRecognitionManager.registerAction(
                new MenuAction("melodies help", () -> {
                    String text = getResources().getString(cosc345.AuralLearner.R.string.melodiesMenu_difficultyHelpText);
                    TextToSpeechManager.getInstance().speak(text);
                }));

        for (Difficulty d: Difficulty.values()) {
            voiceRecognitionManager.registerAction(
                    new MenuAction("melodies " + d.toString().toLowerCase(), () -> {
                        Intent intent = new Intent(MainActivity.this, MelodiesExercise.class);
                        intent.putExtra("EXTRA_DIFFICULTY", d.toString());
                        startActivity(intent);
                    }));

            voiceRecognitionManager.registerAction(
                    new MenuAction("melodies " + d.toString().toLowerCase() + " help", () -> {
                        String text = getResources().getString(cosc345.AuralLearner.R.string.melodiesMenu_difficultyHelpTitle);
                        TextToSpeechManager.getInstance().speak(text);
                    }));
        }
    }

    private void setupMenuButtons() {
        setupMenuButton(cosc345.AuralLearner.R.id.pitchMatchingMenuBtn, PitchMatchingExercise.class);
        setupMenuButton(cosc345.AuralLearner.R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(cosc345.AuralLearner.R.id.scalesMenuBtn, ScalesExercise.class);
        setupMenuButton(cosc345.AuralLearner.R.id.melodiesMenuBtn, MelodiesMenu.class);
        findViewById(cosc345.AuralLearner.R.id.voiceControlHelpBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(cosc345.AuralLearner.R.string.voiceControlHelpTitle)
                    .setMessage(cosc345.AuralLearner.R.string.voiceControlHelp)
                    .setPositiveButton(cosc345.AuralLearner.R.string.dialogOk, null);
            builder.create()
                    .show();
        });
        findViewById(cosc345.AuralLearner.R.id.aboutBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(cosc345.AuralLearner.R.string.aboutTitle)
                    .setMessage(cosc345.AuralLearner.R.string.aboutText)
                    .setPositiveButton(cosc345.AuralLearner.R.string.dialogOk, null);
            builder.create()
                    .show();
        });
        findViewById(cosc345.AuralLearner.R.id.terminologyBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(cosc345.AuralLearner.R.string.terminologyTitle)
                    .setMessage(cosc345.AuralLearner.R.string.terminologyText)
                    .setPositiveButton(cosc345.AuralLearner.R.string.dialogOk, null);
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
    protected void onResume() {
        super.onResume();

        VoiceRecognitionManager.getInstance().restart();
        TextToSpeechManager.getInstance().restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VoiceRecognitionManager.getInstance().close();
        TextToSpeechManager.getInstance().close();
    }

    private AlertDialog createPermissionsExplanationDialog(DialogInterface.OnClickListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(cosc345.AuralLearner.R.string.permissionsExplanationTitle)
                .setMessage(cosc345.AuralLearner.R.string.permissionsExplanation)
                .setPositiveButton(cosc345.AuralLearner.R.string.dialogOk, callback);
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

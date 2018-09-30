package cosc345.app.controller;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Melody;
import cosc345.app.model.MelodyExerciseGrader;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;

public class MelodiesExercise extends VoiceControlActivity implements Playable.Delegate {
    private Button startBtn;
    private Button stopBtn;
    private Melody targetMelody;

    private MelodyExerciseGrader melodyExerciseGrader;
    private Difficulty difficulty;
    private int timesPlayed;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melodies_exercise);

        startBtn = findViewById(R.id.melodiesExercise_startBtn);
        stopBtn = findViewById(R.id.melodiesExercise_stopBtn);

        startBtn.setOnClickListener(v -> startExercise());
        stopBtn.setOnClickListener(v -> stopExercise());

        String difficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");

        if (difficulty.equals(Difficulty.EASY.toString())) {
            this.difficulty = Difficulty.EASY;
        } else if (difficulty.equals(Difficulty.MEDIUM.toString())) {
            this.difficulty = Difficulty.MEDIUM;
        } else {
            this.difficulty = Difficulty.HARD;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopExercise();
        TextToSpeechManager.getInstance().close();
    }

    private void startExercise() {
        // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
        VoiceRecognitionManager.getInstance().close();

        timesPlayed = 0;

        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);

        melodyExerciseGrader = new MelodyExerciseGrader(difficulty);
        melodyExerciseGrader.setOnSuccessCallback(this::onGradingDone);
        melodyExerciseGrader.setCallback(this::showStartButton);
        targetMelody = melodyExerciseGrader.melody;
        targetMelody.setDelegate(this);
        targetMelody.play();
    }


    private void stopExercise() {
        if (melodyExerciseGrader != null) {
            melodyExerciseGrader.stop();
            melodyExerciseGrader.melody.stop();
        }

        showStartButton();
    }

    @Override
    public void onPlaybackStarted() {
        timesPlayed++;
    }

    @Override
    public void onPlaybackFinished() {
        if (timesPlayed < 2) {
            handler.postDelayed(targetMelody::play, Note.NoteLengthMap.get(Note.NoteLength.CROTCHET));
        } else {
            melodyExerciseGrader.start();
        }
    }

    @Override
    public void onDone() {

    }

    private void onGradingDone() {
        double grade = melodyExerciseGrader.getScore();
        String msg;

        if (grade < 60.0) {
            msg = "Your score was bad";
        } else if (grade < 80.0) {
            msg = "Your score was ok";
        } else if (grade < 90.0) {
            msg = "Your score was good";
        } else {
            msg = "Your score was perfect";
        }

        TextToSpeechManager.getInstance().speak(msg);
    }

    private void showStartButton() {
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);
    }
}


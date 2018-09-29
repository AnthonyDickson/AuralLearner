package cosc345.app.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Interval;
import cosc345.app.model.IntervalExerciseGrader;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;

public class IntervalsExercise extends VoiceControlActivity implements Playable.Delegate {
    private Button startBtn;
    private Button stopBtn;
    Interval targetInterval;

    private IntervalExerciseGrader intervalExerciseGrader;
    private Difficulty difficulty;
    private int timesPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals_exercise);

        startBtn = findViewById(R.id.intervalsExercise_startBtn);
        stopBtn = findViewById(R.id.intervalsExercise_stopBtn);

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

        intervalExerciseGrader = new IntervalExerciseGrader(difficulty);
        intervalExerciseGrader.setOnSuccessCallback(this::onGradingDone);
        intervalExerciseGrader.setCallback(this::showStartButton);
        targetInterval = intervalExerciseGrader.interval;
        targetInterval.setDelegate(this);
        targetInterval.play();
    }


    private void stopExercise() {
        if (intervalExerciseGrader != null) {
            intervalExerciseGrader.stop();
            intervalExerciseGrader.interval.stop();
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
            targetInterval.play();
        } else {
            intervalExerciseGrader.start();
        }
    }

    @Override
    public void onDone() {

    }

    private void onGradingDone() {
        double grade = intervalExerciseGrader.getScore();
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

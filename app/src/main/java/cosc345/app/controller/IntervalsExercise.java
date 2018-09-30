package cosc345.app.controller;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Interval;
import cosc345.app.model.IntervalExerciseGrader;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;

public class IntervalsExercise extends ExerciseActivity implements Playable.Delegate {
    private Difficulty difficulty;

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
    protected void startExercise() {
        super.startExercise();

        grader = new IntervalExerciseGrader(difficulty);
        grader.setOnSuccessCallback(this::onGradingDone);
        grader.setCallback(this::showStartButton);
        target = grader.playable;
        target.setDelegate(this);
        target.play();
    }
}

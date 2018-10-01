package cosc345.AuralLearner.controller;

import android.os.Bundle;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.Difficulty;
import cosc345.AuralLearner.model.MelodyExerciseGrader;
import cosc345.AuralLearner.model.Playable;

public class MelodiesExercise extends ExerciseActivity implements Playable.Delegate {
    private Difficulty difficulty;

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
    protected void startExercise() {
        super.startExercise();

        grader = new MelodyExerciseGrader(difficulty);
        grader.setOnSuccessCallback(this::onGradingDone);
        grader.setCallback(this::showStartButton);
        target = grader.playable;
        target.setDelegate(this);
        target.play();
    }
}


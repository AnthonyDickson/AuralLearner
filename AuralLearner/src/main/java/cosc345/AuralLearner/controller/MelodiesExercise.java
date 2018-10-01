package cosc345.AuralLearner.controller;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.Difficulty;
import cosc345.AuralLearner.model.MelodyExerciseGrader;

public class MelodiesExercise extends ExerciseActivity {
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

        TextView title = findViewById(R.id.title);
        title.append(String.format(Locale.ENGLISH, " (%s)", difficulty));
    }

    @Override
    protected void setupExercise() {
        grader = new MelodyExerciseGrader(difficulty);
    }
}


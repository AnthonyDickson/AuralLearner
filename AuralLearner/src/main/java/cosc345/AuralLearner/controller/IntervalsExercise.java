package cosc345.AuralLearner.controller;

import android.os.Bundle;

import java.util.Locale;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.Difficulty;
import cosc345.AuralLearner.model.IntervalExerciseGrader;

public class IntervalsExercise extends ExerciseActivity {
    private Difficulty difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String difficulty = getIntent().getStringExtra("EXTRA_DIFFICULTY");

        if (difficulty.equals(Difficulty.EASY.toString())) {
            this.difficulty = Difficulty.EASY;
        } else if (difficulty.equals(Difficulty.MEDIUM.toString())) {
            this.difficulty = Difficulty.MEDIUM;
        } else {
            this.difficulty = Difficulty.HARD;
        }

        title.setText(String.format(Locale.ENGLISH, "%s (%s)",
                getResources().getString(R.string.intervals_title), difficulty));
        shortDescription.setText(R.string.intervals_short_desc);
    }

    @Override
    void setupExercise() {
        grader = new IntervalExerciseGrader(difficulty);
    }
}

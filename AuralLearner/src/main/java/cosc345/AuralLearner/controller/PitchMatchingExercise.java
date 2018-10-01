package cosc345.AuralLearner.controller;

import android.os.Bundle;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.PitchMatchingExerciseGrader;

/**
 * Activity that allows the user to try to match a pitch.
 */
public class PitchMatchingExercise extends ExerciseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title.setText(getResources().getString(R.string.pitchMatching_title));
        shortDescription.setText(R.string.pitchMatching_short_desc);
    }

    @Override
    void setupExercise() {
        grader = new PitchMatchingExerciseGrader();
    }
}

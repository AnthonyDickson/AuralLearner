package cosc345.AuralLearner.controller;

import android.os.Bundle;

import java.util.Locale;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.ScaleExerciseGrader;

public class ScalesExercise extends ExerciseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ScaleExerciseGrader.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        title.setText(getResources().getString(R.string.scales_title));
        shortDescription.setText(R.string.scales_short_desc);
    }

    @Override
    protected void setupExercise() {
        grader = new ScaleExerciseGrader();
    }
}

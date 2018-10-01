package cosc345.AuralLearner.controller;

import android.os.Bundle;

import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.ScaleExerciseGrader;

public class ScalesExercise extends ExerciseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scales_exercise);

        startBtn = findViewById(R.id.scalesExercise_startBtn);
        stopBtn = findViewById(R.id.scalesExercise_stopBtn);

        startBtn.setOnClickListener(v -> startExercise());
        stopBtn.setOnClickListener(v -> stopExercise());
        try {
            ScaleExerciseGrader.class.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setupExercise() {
        grader = new ScaleExerciseGrader();
    }
}

package cosc345.AuralLearner.controller;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import cosc345.AuralLearner.R;
import cosc345.AuralLearner.model.Note;
import cosc345.AuralLearner.model.PitchDetector;
import cosc345.AuralLearner.model.PitchMatchingExerciseGrader;
import cosc345.AuralLearner.model.Playable;
import cosc345.AuralLearner.model.TextToSpeechManager;
import cosc345.AuralLearner.model.VoiceRecognitionManager;

/**
 * Activity that allows the user to try to match a pitch.
 */
public class PitchMatchingExercise extends ExerciseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_matching);

        startBtn = findViewById(R.id.pitchMatching_startBtn);
        stopBtn = findViewById(R.id.pitchMatching_stopBtn);

        startBtn.setOnClickListener(v -> startExercise());
        stopBtn.setOnClickListener(v -> stopExercise());
    }

    @Override
    void setupExercise() {
        grader = new PitchMatchingExerciseGrader();
    }
}

package cosc345.app.controller;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.model.Callback;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Interval;
import cosc345.app.model.IntervalExerciseGrader;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;

public class IntervalExercise extends AppCompatActivity implements Playable.Delegate {
    private Button startBtn;
    private Button stopBtn;
    Interval targetInterval;

    private IntervalExerciseGrader intervalExerciseGrader;
    public TextToSpeechManager tts;
    private Difficulty difficulty;
    private int timesPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_exercise);

        startBtn = findViewById(R.id.interval_startBtn);
        stopBtn = findViewById(R.id.interval_stopBtn);

        //targetIntervalView = findViewById(R.id.interval_targetName);
        //scoreView = findViewById(R.id.interval_scoreText);

        startBtn.setOnClickListener(v -> startExercise()); //startExercise listening, play exercise
        stopBtn.setOnClickListener(v -> stopExercise());

        tts = TextToSpeechManager.getInstance();
        tts.init(this, null, null);

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
        tts.close();
    }

    private void startExercise() {
        // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
        VoiceRecognitionManager.getInstance().close();

        timesPlayed = 0;

        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);

        intervalExerciseGrader = new IntervalExerciseGrader(difficulty);
        intervalExerciseGrader.setOnSuccessCallback(this::onGradingDone);
        intervalExerciseGrader.setCallback(this::onDone);
        targetInterval = intervalExerciseGrader.interval;
        targetInterval.setDelegate(this);
        targetInterval.play();
    }


    private void stopExercise() {
        targetInterval.stop();
        intervalExerciseGrader.stop();

        onDone();
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

    private void onGradingDone() {
        double grade = intervalExerciseGrader.getScore();

        if (grade < 60.0) {
            tts.speak("Your score was bad");
        } else if (grade < 80.0) {
            tts.speak("Your score was ok");
        } else if (grade < 90.0) {
            tts.speak("Your score was good");
        } else {
            tts.speak("Your score was perfect");
        }
    }

    private void onDone() {
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);}
}

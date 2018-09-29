package cosc345.app.controller;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Interval;
import cosc345.app.model.IntervalExerciseGrader;
import cosc345.app.model.Playable;
import cosc345.app.model.VoiceRecognitionManager;

public class IntervalExercise extends AppCompatActivity implements Playable.Delegate {
    private static final String LOG_TAG = "IntervalExercise";
    private Button startBtn;
    private Button stopBtn;
    Interval targetInterval;

    private IntervalExerciseGrader intervalExerciseGrader;
    public TextToSpeech tts;
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

        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.UK);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.i(LOG_TAG, "Utterance started");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.i(LOG_TAG, "Utterance finished");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e(LOG_TAG, String.format("An error occurred when speaking utterance %s.", utteranceId));
                    }
                });
                Log.i(LOG_TAG, "Initialisation Complete.");
            }
        });

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
        tts.shutdown();
    }

    private void startExercise() {
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
        targetInterval.stop();
        intervalExerciseGrader.stop();

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

        if (grade < 60.0) {
            tts.speak("Your score was bad", TextToSpeech.QUEUE_FLUSH, null);
        } else if (grade < 80.0) {
            tts.speak("Your score was ok", TextToSpeech.QUEUE_FLUSH, null);
        } else if (grade < 90.0) {
            tts.speak("Your score was good", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak("Your score was perfect", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void showStartButton() {
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);}
}

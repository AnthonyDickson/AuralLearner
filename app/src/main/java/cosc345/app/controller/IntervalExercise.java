package cosc345.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.MainActivity;
import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Interval;
import cosc345.app.model.IntervalExerciseCreator;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;
import cosc345.app.model.VoiceRecognitionManager;

import cosc345.app.model.Intervals;
import static cosc345.app.model.Intervals.P5;
import static cosc345.app.model.Note.NoteLength.MINIM;
import static cosc345.app.model.Utilities.random;

public class IntervalExercise extends AppCompatActivity implements Playable.PlayableDelegate {
    private boolean isListening, isPlaying;
    private Button startBtn;
    private Button stopBtn;
    private Button playTargetBtn;
    private Button stopTargetBtn;
    Interval targetInterval;

    private IntervalExerciseCreator intervalExerciseCreator;
    private TextView scoreView;
    public TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_exercise);

        isListening = false;
        isPlaying = false;

        startBtn = findViewById(R.id.interval_startBtn);
        stopBtn = findViewById(R.id.interval_stopBtn);

        //targetIntervalView = findViewById(R.id.interval_targetName);
        //scoreView = findViewById(R.id.interval_scoreText);

        startBtn.setOnClickListener(v -> startListening()); //start listening, play exercise
        stopBtn.setOnClickListener(v -> stopListening());

        //Text to speech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        stopListening();

    }

    private void startListening() {
        if (!isListening) {
            if (isPlaying) {

            }

            // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
            VoiceRecognitionManager.getInstance().close();

            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            if (getIntent().getStringExtra("EXTRA_DIFFICULTY").equals("Easy")){
                intervalExerciseCreator = new IntervalExerciseCreator(Difficulty.EASY);

            } else if (getIntent().getStringExtra("EXTRA_DIFFICULTY").equals("Medium")){
                intervalExerciseCreator = new IntervalExerciseCreator(Difficulty.MEDIUM);

            } else{
                intervalExerciseCreator = new IntervalExerciseCreator(Difficulty.HARD);

            }
            targetInterval = intervalExerciseCreator.interval;//playthis
            targetInterval.play();
            targetInterval.play();
            intervalExerciseCreator.start();

            double grade = intervalExerciseCreator.getScore() * 100;

            if (grade <60.0) {
                tts.speak("Your score was bad", TextToSpeech.QUEUE_ADD, null);
            } else if (grade >60.0 && grade <80.0){
                tts.speak("Your score was ok", TextToSpeech.QUEUE_ADD, null);
            } else if (grade < 90.0){
                tts.speak("Your score was good", TextToSpeech.QUEUE_ADD, null);
            } else {
                tts.speak("Your score was perfect", TextToSpeech.QUEUE_ADD, null);
            }
        }

    }


    private void stopListening() {
        if (isListening) {
            stopBtn.setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);
            intervalExerciseCreator.stop();
            isListening = false;
        }
    }
    @Override
    public void onPlaybackStarted() {
        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaybackFinished() {
        stopBtn.setVisibility(View.GONE);
        startBtn.setVisibility(View.VISIBLE);
        isPlaying = false;
    }

}

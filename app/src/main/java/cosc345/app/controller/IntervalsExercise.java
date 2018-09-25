package cosc345.app.controller;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.model.Grader;
import cosc345.app.model.Interval;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
<<<<<<< HEAD
import cosc345.app.model.Grader;

import cosc345.app.model.VoiceRecognitionManager;

import static cosc345.app.model.Interval.Intervals;
import static cosc345.app.model.Interval.Intervals.P5;
=======
import cosc345.app.model.VoiceRecognitionManager;

import cosc345.app.model.Intervals;
import static cosc345.app.model.Intervals.P5;
>>>>>>> origin/IntervalExercise_in_progress
import static cosc345.app.model.Note.NoteLength.MINIM;

public class IntervalsExercise extends AppCompatActivity implements Playable.Delegate {
    private boolean isListening, isPlaying;
    private Interval targetInterval;
    private Button startBtn;
    private Button stopBtn;
    private Button playTargetBtn;
    private Button stopTargetBtn;
    private TextView targetIntervalView;
    private int choice;
    private int intervalChoice;
    private Grader grader;
    private TextView scoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals_exercise);

        isListening = false;
        isPlaying = false;

        startBtn = findViewById(R.id.intervals_startBtn);
        stopBtn = findViewById(R.id.intervals_stopBtn);
        playTargetBtn = findViewById(R.id.intervals_playTargetBtn);
        stopTargetBtn = findViewById(R.id.intervals_stopTargetBtn);
        targetIntervalView = findViewById(R.id.intervals_targetName);
        scoreView = findViewById(R.id.intervals_scoreText);

        setTargetInterval(new Interval(new Note("C4", MINIM), P5));
        AlertDialog chooseNoteDialog = createNotePickerDialog();
        AlertDialog chooseIntervalDialog = createIntervalPickerDialog();

        startBtn.setOnClickListener(v -> startListening());
        stopBtn.setOnClickListener(v -> stopListening());
        playTargetBtn.setOnClickListener(v -> startTargetPlayback());
        stopTargetBtn.setOnClickListener(v -> stopTargetPlayback());
        findViewById(R.id.intervals_changeTargetRootBtn).setOnClickListener(v -> {
            stopListening();
            stopTargetPlayback();
            chooseNoteDialog.show();
        });
        findViewById(R.id.intervals_changeTargetIntervalBtn).setOnClickListener(v -> {
            stopListening();
            stopTargetPlayback();
            chooseIntervalDialog.show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopListening();
        stopTargetPlayback();
    }

    private void startListening() {
        if (!isListening) {
            if (isPlaying) {
                stopTargetPlayback();
            }

            // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
            VoiceRecognitionManager.getInstance().close();

            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            grader.start();
            isListening = true;
        }
    }

    private void stopListening() {
        if (isListening) {
            stopBtn.setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);
            grader.stop();
            isListening = false;
        }
    }

    private void startTargetPlayback() {
        if (isListening) {
            stopListening();
        }
        if (isPlaying) {
            stopTargetPlayback();
        }

        playTargetBtn.setVisibility(View.GONE);
        stopTargetBtn.setVisibility(View.VISIBLE);
        targetInterval.play();
        isPlaying = true;
    }

    private void stopTargetPlayback() {
        if (!isPlaying) {
            return;
        }

        targetInterval.stop();
    }

    @Override
    public void onPlaybackStarted() {}

    @Override
    public void onPlaybackFinished() {
        stopTargetBtn.setVisibility(View.GONE);
        playTargetBtn.setVisibility(View.VISIBLE);
        isPlaying = false;
    }

    private AlertDialog createNotePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Note")
                .setSingleChoiceItems(Note.NOTE_NAMES, targetInterval.root.getNameIndex(),
                        (dialog, which) -> choice = which)
                .setPositiveButton(R.string.dialogOk, (dialog, id) -> setTargetRoot(new Note(Note.NOTE_NAMES[choice])))
                .setNeutralButton("Choose For Me", (dialog, id) -> setTargetRoot(Note.getRandom()))
                .setNegativeButton(R.string.dialogCancel, (dialog, id) -> choice = Note.A4_INDEX);

        return builder.create();
    }

    private AlertDialog createIntervalPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Note")
                .setSingleChoiceItems(Interval.getFullNames(), targetInterval.interval.ordinal(),
                        (dialog, which) -> intervalChoice = which)
                .setPositiveButton(R.string.dialogOk, (dialog, id) -> setTargetInterval(new Interval(targetInterval.root, Intervals.values()[intervalChoice])))
<<<<<<< HEAD
                .setNeutralButton("Choose For Me", (dialog, id) -> setTargetInterval(Interval.randomInterval(targetInterval.root)))
                .setNegativeButton(R.string.dialogCancel, (dialog, id) -> intervalChoice = Interval.Intervals.P1.ordinal());
=======
                .setNeutralButton("Choose For Me", (dialog, id) -> setTargetInterval(new Interval(targetInterval.root,
                        // TODO: Refactor the below into the method Interval.getRandom().
                        Intervals.values()[random.nextInt(Intervals.values().length)])))
                .setNegativeButton(R.string.dialogCancel, (dialog, id) -> intervalChoice = Intervals.P1.ordinal());
>>>>>>> origin/IntervalExercise_in_progress

        return builder.create();
    }

    private void setTargetRoot(Note note) {
        setTargetInterval(new Interval(note, targetInterval.interval));
    }

    private void setTargetInterval(Interval interval) {
        targetInterval = interval;
        targetInterval.root.setNoteLength(Note.NoteLength.MINIM);
        targetInterval.other.setNoteLength(Note.NoteLength.MINIM);
        targetInterval.setDelegate(this);
        targetIntervalView.setText(targetInterval.toString());

        for (Note note : targetInterval.getNotes()) {
            Log.d("Intervals Exercise", String.format("Note with frequency %f with a duration of %d ms.", note.getFrequency(), note.getDuration()));
        }
        grader = new Grader(targetInterval.getNotes());
        grader.setCallback(this::onGradingDone);
    }

    private void onGradingDone() {
        scoreView.setText(String.format(Locale.ENGLISH, "%.2f", grader.getScore()));
        stopBtn.setVisibility(View.GONE);
        startBtn.setVisibility(View.VISIBLE);
        isListening = false;
    }
}

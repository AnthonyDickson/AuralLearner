package cosc345.app.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.lib.Note;
import cosc345.app.model.FFT;
import cosc345.app.model.PlayableNote;
import cosc345.app.model.VoiceRecognitionManager;

/**
 * Activity that allows the user to try to match a pitch.
 */
public class PitchMatchingExercise extends AppCompatActivity implements FFT.FFTResultListener {
    private static final double VOLUME_THRESHOLD = 8e9;
    private static final int MATCH_THRESHOLD_CENTS = 10;
    private boolean isListening, isPlaying;
    private PlayableNote playableNote;
    private Thread fftThread, notePlayerThread;
    private Note targetNote, userNote;
    private Button start;
    private Button stop;
    private Button playTargetPitch;
    private Button stopTargetPitch;
    private TextView targetPitchView, userPitchView, pitchDifferenceView;
    private AlertDialog chooseNoteDialog;
    private ColorStateList defaultColours;
    private int choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_matching);

        isListening = false;
        isPlaying = false;
        userNote = null;

        start = findViewById(R.id.pitchMatching_startBtn);
        stop = findViewById(R.id.pitchMatching_stopBtn);
        playTargetPitch = findViewById(R.id.pitchMatching_playTargetPitchBtn);
        stopTargetPitch = findViewById(R.id.pitchMatching_stopTargetPitchBtn);
        targetPitchView = findViewById(R.id.pitchMatching_targetPitchText);
        userPitchView = findViewById(R.id.pitchMatching_userPitchText);
        pitchDifferenceView = findViewById(R.id.pitchMatching_pitchDifferenceText);
        defaultColours = pitchDifferenceView.getTextColors();

        setTargetPitchView(new Note("C4"));
        chooseNoteDialog = createNotePickerDialog();

        start.setOnClickListener(v -> startListening());
        stop.setOnClickListener(v -> stopListening());
        playTargetPitch.setOnClickListener(v -> startTargetPitchPlayback());
        stopTargetPitch.setOnClickListener(v -> stopTargetPitchPlayback());
        findViewById(R.id.pitchMatching_changeTargetPitchBtn).setOnClickListener(v -> {
            stopListening();
            stopTargetPitchPlayback();
            chooseNoteDialog.show();
        });
    }

    private void startListening() {
        if (!isListening) {
            if (isPlaying) {
                stopTargetPitchPlayback();
            }

            // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
            VoiceRecognitionManager.getInstance().close();

            start.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            fftThread = new Thread(new FFT(this));
            fftThread.start();
            isListening = true;
        }
    }

    private void stopListening() {
        if (isListening) {
            resetUI();
            stop.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
            fftThread.interrupt();
            fftThread = null;
            isListening = false;
        }
    }

    private void startTargetPitchPlayback() {
        if (isListening) {
            stopListening();
        }
        if (isPlaying) {
            stopTargetPitchPlayback();
        }

        resetUI();
        playTargetPitch.setVisibility(View.GONE);
        stopTargetPitch.setVisibility(View.VISIBLE);
        playableNote = new PlayableNote(targetNote);
        playableNote.setCallback(this::onPlaybackDone);
        notePlayerThread = new Thread(playableNote);
        notePlayerThread.start();
        isPlaying = true;
    }

    private void stopTargetPitchPlayback() {
        if (!isPlaying) {
            return;
        }

        playableNote.stop();
        notePlayerThread.interrupt();
        onPlaybackDone();
    }

    /**
     * Callback for when the tone is finished playing back.
     */
    private void onPlaybackDone() {
        stopTargetPitch.setVisibility(View.GONE);
        playTargetPitch.setVisibility(View.VISIBLE);
        isPlaying = false;
    }

    /**
     * Clear the user's pitch and the pitch difference text views.
     */
    private void resetUI() {
        userPitchView.setText("-");
        pitchDifferenceView.setText("-");
        pitchDifferenceView.setTextColor(defaultColours);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopListening();
        stopTargetPitchPlayback();
    }

    @Override
    public void onFFTResult(double frequency, double amplitude, double averageFrequency,
                            double[] recentFrequencies) {
        if (!isListening || amplitude < PitchMatchingExercise.VOLUME_THRESHOLD) {
            resetUI();
            return;
        }

        try {
            userNote = new Note(averageFrequency);
            userPitchView.setText(userNote.getName());
            int halfstepDiff = userNote.compareTo(targetNote);
            int centDiff = Note.centDistance(userNote, targetNote) % 100;
            pitchDifferenceView.setText(String.format(Locale.ENGLISH,
                    "%d semitone(s) and %d cent(s)", halfstepDiff, centDiff));

            if (halfstepDiff == 0) {
                if (Math.abs(userNote.getCents()) < PitchMatchingExercise.MATCH_THRESHOLD_CENTS) {
                    pitchDifferenceView.setTextColor(Color.GREEN);
                } else {
                    pitchDifferenceView.setTextColor(defaultColours);
                }
            }
        } catch (IllegalArgumentException e) {
            resetUI();
        }
    }

    private AlertDialog createNotePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Note")
                .setSingleChoiceItems(Note.NOTE_NAMES, targetNote.getNameIndex(),
                        (dialog, which) -> choice = which)
                .setPositiveButton(R.string.dialogOk, (dialog, id) -> setTargetPitchView(new Note(Note.NOTE_NAMES[choice])))
                .setNeutralButton("Choose For Me", (dialog, id) -> setTargetPitchView(Note.getRandom()))
                .setNegativeButton(R.string.dialogCancel, (dialog, id) -> choice = Note.A4_INDEX);

        return builder.create();
    }

    private void setTargetPitchView(Note note) {
        note.setDuration(Note.NoteLength.SEMIBREVE, false);
        targetNote = note;
        targetPitchView.setText(note.getName());
    }
}

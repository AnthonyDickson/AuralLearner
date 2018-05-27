package cosc345.app.view;

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
import cosc345.app.model.NotePlayer;
/* TODO: Change so that the target pitch is played back after the user sings the note automatically, and repeat. */
/**
 * Activity that allows the user to try to match a pitch.
 */
public class PitchMatching extends AppCompatActivity implements FFT.FFTResultListener {
    private static final double VOLUME_THRESHOLD = 8e9;
    private static final int PLAYBACK_DURATION = 3; //  in seconds
    private boolean isListening, isPlaying;
    private NotePlayer notePlayer;
    private Thread fftThread, notePlayerThread;
    private Note targetNote, userNote;
    private Button start;
    private Button stop;
    private Button playTargetPitch;
    private Button stopTargetPitch;
    private TextView targetPitch, userPitch, pitchDifference;
    private AlertDialog chooseNoteDialog;
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
        targetPitch = findViewById(R.id.pitchMatching_targetPitchText);
        userPitch = findViewById(R.id.pitchMatching_userPitchText);
        pitchDifference = findViewById(R.id.pitchMatching_pitchDifferenceText);
        chooseNoteDialog = createNotePickerDialog();

        setTargetPitch(new Note("C4"));
        start.setOnClickListener(v -> startListening());
        stop.setOnClickListener(v -> stopListening());
        playTargetPitch.setOnClickListener(v -> startTargetPitchPlayback());
        stopTargetPitch.setOnClickListener(v -> stopTargetPitchPlayback());
        findViewById(R.id.pitchMatching_changeTargetPitchBtn).setOnClickListener(v -> {
            stopTargetPitchPlayback();
            chooseNoteDialog.show();
        });
    }

    private void startListening() {
        if (!isListening) {
            if (isPlaying) {
                stopTargetPitchPlayback();
            }

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
        notePlayer = new NotePlayer(targetNote.getFrequency(), PitchMatching.PLAYBACK_DURATION, this::onPlaybackDone);
        notePlayerThread = new Thread(notePlayer);
        notePlayerThread.start();
        isPlaying = true;
    }

    private void stopTargetPitchPlayback() {
        if (!isPlaying) {
            return;
        }

        notePlayer.stop();
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
        userPitch.setText("-");
        pitchDifference.setText("-");
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
        if (!isListening || amplitude < PitchMatching.VOLUME_THRESHOLD) {
            resetUI();
            return;
        }

        try {
            userNote = new Note(frequency);
            userPitch.setText(userNote.getName());
            int diff = userNote.compareTo(targetNote);
            pitchDifference.setText(String.format(Locale.ENGLISH, "%d semitones", diff));
        } catch (IllegalArgumentException e) {
            resetUI();
        }
    }

    private AlertDialog createNotePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Note")
                .setSingleChoiceItems(Note.NOTE_NAMES, Note.A4_INDEX,
                        (dialog, which) -> choice = which)
                .setPositiveButton("Ok", (dialog, id) -> setTargetPitch(new Note(Note.NOTE_NAMES[choice])))
                .setNeutralButton("Choose For Me", (dialog, id) -> setTargetPitch(Note.getRandom()))
                .setNegativeButton("Cancel", (dialog, id) -> choice = Note.A4_INDEX);

        return builder.create();
    }

    private void setTargetPitch(Note note) {
        targetNote = note;
        targetPitch.setText(note.getName());
    }
}

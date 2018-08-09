package cosc345.app.view.testing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import cosc345.app.R;
import cosc345.app.lib.Note;
import cosc345.app.model.PlayableNote;

/**
 * An activity to test the functionality of <code>NotePlayer</code>.
 */
public class PlayNote extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private PlayableNote notePlayer;
    private Thread notePlayerThread;
    private double freqOfTone;
    private boolean isPlaying;
    private Button play, stop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_note);

        freqOfTone = 440.0; //Hz

        Spinner spinner = findViewById(R.id.noteSelectionSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Note.NOTE_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(Note.A4_INDEX);

        play = findViewById(R.id.playNoteBtn);
        play.setOnClickListener(v -> play());
        stop = findViewById(R.id.stopNoteBtn);
        stop.setOnClickListener(v -> stop());
    }

    private void play() {
        if (isPlaying) {
            return;
        }

        notePlayer = new PlayableNote(freqOfTone, Note.NoteLength.SEMIBREVE, false);
        notePlayer.setCallback(this::onPlayBackDone);
        notePlayerThread = new Thread(notePlayer);
        notePlayerThread.start();
        play.setVisibility(View.GONE);
        stop.setVisibility(View.VISIBLE);
        isPlaying = true;
    }

    private void stop() {
        if (!isPlaying) {
            return;
        }

        notePlayer.stop();
        notePlayerThread.interrupt();
        onPlayBackDone();
    }

    private void onPlayBackDone() {
        play.setVisibility(View.VISIBLE);
        stop.setVisibility(View.GONE);
        isPlaying = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String noteName = Note.NOTE_NAMES[position];
        Note note = new Note(noteName);
        freqOfTone = note.getFrequency();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        freqOfTone = Note.A4_FREQUENCY;
    }

    @Override
    protected void onPause() {
        super.onPause();

        stop();
    }
}

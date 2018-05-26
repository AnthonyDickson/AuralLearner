package cosc345.app.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import cosc345.app.R;
import cosc345.app.lib.Note;
import cosc345.app.lib.NotePlayer;

/**
 * An activity to test the functionality of <code>NotePlayer</code>.
 */
public class PlayNote extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Thread notePlayerThread;
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private double freqOfTone = 440; // hz

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_note);

        Spinner spinner = findViewById(R.id.noteSelectionSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Note.NOTE_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(Note.A4_INDEX);

        Button btn = findViewById(R.id.playNoteBtn);
        btn.setOnClickListener(v -> {
            if (notePlayerThread != null) {
                notePlayerThread.interrupt();
            }

            notePlayerThread = new Thread(new NotePlayer(freqOfTone, 3, null));
            notePlayerThread.start();
        });
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
}

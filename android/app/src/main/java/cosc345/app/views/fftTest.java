package cosc345.app.views;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.lib.FFT;
import cosc345.app.lib.Note;

/**
 * An activity to test the functionality of the FFT class.
 */
public class fftTest extends AppCompatActivity {
    private static final double UPDATE_THRESHOLD = 8e9;

    Thread fftThread;
    TextView frequencyOutput;
    TextView noteOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft_test);

        frequencyOutput = findViewById(R.id.fftFrequencyTextView);
        noteOutput = findViewById(R.id.noteTextView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        fftThread = new Thread(new FFT(this));
        fftThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        fftThread.interrupt();
        fftThread = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Use the data from the FFT algorithm to update the UI.
     *
     * @param frequency    the 'best' frequency of the mic input.
     * @param avgFrequency the frequency calculated as a moving average.
     * @param amplitude    the 'best' amplitude of the mic input.
     */
    public void updateUI(double frequency, double avgFrequency, double amplitude) {
        if (amplitude < UPDATE_THRESHOLD) {
            frequencyOutput.setText("-");
            noteOutput.setText("-");
            return;
        }

        try {
            Note note = new Note(avgFrequency);
            frequencyOutput.setText(String.format(Locale.ENGLISH, "%.2f (Avg: %.2f)", frequency,
                    avgFrequency));
            noteOutput.setText(String.format(Locale.ENGLISH, "%s %d cents", note, note.getCents()));
        } catch (IllegalArgumentException e) {
            frequencyOutput.setText("-");
            noteOutput.setText("-");
        }
    }
}

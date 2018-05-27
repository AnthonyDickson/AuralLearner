package cosc345.app.view.testing;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import cosc345.app.R;
import cosc345.app.lib.Note;
import cosc345.app.model.FFT;

/**
 * An activity to test the functionality of the FFT class.
 */
public class fftTest extends AppCompatActivity implements FFT.FFTResultListener {
    private static final double UPDATE_THRESHOLD = 8e9;

    private Thread fftThread;
    private TextView frequencyOutput;
    private TextView noteOutput;

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


    @Override
    public void onFFTResult(double frequency, double amplitude, double averageFrequency, double[] recentFrequencies) {
        if (amplitude < fftTest.UPDATE_THRESHOLD) {
            frequencyOutput.setText("-");
            noteOutput.setText("-");
            return;
        }

        try {
            Note note = new Note(averageFrequency);
            frequencyOutput.setText(String.format(Locale.ENGLISH, "%.2f (Avg: %.2f)", frequency,
                    averageFrequency));
            noteOutput.setText(String.format(Locale.ENGLISH, "%s %d cents", note, note.getCents()));
        } catch (IllegalArgumentException e) {
            frequencyOutput.setText("-");
            noteOutput.setText("-");
        }
    }
}

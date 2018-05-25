package cosc345.app.views;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

import cosc345.app.R;
import cosc345.app.lib.FFT;
import cosc345.app.lib.VoiceRecognitionManager;

/**
 * An activity to test the functionality of the FFT class.
 */
public class fftTest extends AppCompatActivity {
    Thread fftThread;
    TextView frequencyOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft_test);

        frequencyOutput = findViewById(R.id.fftFrequencyTextView);

        fftThread = new Thread(new FFT(this, new Handler()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        VoiceRecognitionManager.getInstance().close();
        fftThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        fftThread.interrupt();
        VoiceRecognitionManager.getInstance().resume();
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
     * @param frequency the 'best' frequency.
     * @param frequencies the frequencies calculated from the last recorded audio chunk.
     */
    public void updateUI(double frequency, Map<Double, Double> frequencies) {
        frequencyOutput.setText(String.format(Locale.ENGLISH, "%.2f", frequency));
    }
}

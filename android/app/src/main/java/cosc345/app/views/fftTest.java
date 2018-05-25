package cosc345.app.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

import cosc345.app.R;
import cosc345.app.lib.FFT;

public class fftTest extends AppCompatActivity {
    Thread fftThread;
    TextView frequencyOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fft_test);

        frequencyOutput = findViewById(R.id.fftFrequencyTextView);

        fftThread = new Thread(new FFT(this, new Handler()));
        fftThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fftThread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fftThread.interrupt();
    }

    public void updateUI(double frequency, Map<Double, Double> frequencies) {
        frequencyOutput.setText(String.format(Locale.ENGLISH, "%.2f", frequency));
    }
}

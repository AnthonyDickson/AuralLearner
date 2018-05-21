package cosc345.app.lib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cosc345.app.views.fftTest;

public class FFT implements Runnable {

    private final static int RATE = 8000;
    private final static int CHANNEL_MODE = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final static int BUFFER_SIZE_IN_MS = 3000;
    private final static int CHUNK_SIZE_IN_SAMPLES = 4096; // = 2 ^
    // CHUNK_SIZE_IN_SAMPLES_POW2
    private final static int CHUNK_SIZE_IN_MS = 1000 * CHUNK_SIZE_IN_SAMPLES
            / RATE;
    private final static int BUFFER_SIZE_IN_BYTES = RATE * BUFFER_SIZE_IN_MS
            / 1000 * 2;
    private final static int CHUNK_SIZE_IN_BYTES = RATE * CHUNK_SIZE_IN_MS
            / 1000 * 2;
    private final static int MIN_FREQUENCY = 50; // HZ
    private final static int MAX_FREQUENCY = 600; // HZ - it's for guitar, should be enough
    private final static int DRAW_FREQUENCY_STEP = 5;
    private static final String LOG_TAG = "FFT";

    private final fftTest parent;
    private final android.os.Handler handler;
    private AudioRecord recorder;

    public FFT(fftTest parent, android.os.Handler handler) {
        this.parent = parent;
        this.handler = handler;
    }

    public static void DoFFT(double[] data, int nn) {
        long n, mmax, m, istep;
        int j, i;
        double wtemp, wr, wpr, wpi, wi, theta;
        double tempr, tempi, tempj;

        //reverse-binary reindexing
        n = nn << 1;
        j = 1;
        for (i = 1; i < n; i += 2) {
            if (j > i) {
                tempj = data[j - 1];
                data[j - 1] = data[i - 1];
                data[i - 1] = tempj;
                tempj = data[j];
                data[j] = data[i];
                data[i] = tempj;
            }
            m = nn;
            while (m >= 2 && j > m) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }

        //here begins the Danielson-Lanczos section
        mmax = 2;
        while (n > mmax) {
            istep = mmax << 1;
            theta = -(2 * Math.PI / mmax); //check this
            wtemp = Math.sin(0.5 * theta); //check this
            wpr = -2.0 * wtemp * wtemp;
            wpi = Math.sin(theta); //check this
            wr = 1.0;
            wi = 0.0;
            for (m = 1; m < mmax; m += 2) {
                for (i = (int) m; i <= n; i += istep) {
                    j = (int) (i + mmax);
                    tempr = wr * data[j - 1] - wi * data[j];
                    tempi = wr * data[j] + wi * data[j - 1];
                    data[j - 1] = data[i - 1] - tempr;
                    data[j] = data[i] - tempi;
                    data[i - 1] += tempr;
                    data[i] += tempi;
                }
                wtemp = wr;
                wr += wr * wpr - wi * wpi;
                wi += wi * wpr + wtemp * wpi;
            }
            mmax = istep;
        }
    }

    public void run() {
        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RATE, CHANNEL_MODE,
                ENCODING, 6144);

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Can't initialize AudioRecord");
            return;
        } else {
            Log.i(LOG_TAG, "FFT thread started.");
        }

        short[] audio_data = new short[BUFFER_SIZE_IN_BYTES / 2];
        double[] data = new double[CHUNK_SIZE_IN_SAMPLES * 2];
        final int min_frequency_fft = Math.round(MIN_FREQUENCY
                * CHUNK_SIZE_IN_SAMPLES / RATE);
        final int max_frequency_fft = Math.round(MAX_FREQUENCY
                * CHUNK_SIZE_IN_SAMPLES / RATE);
        while (!Thread.interrupted()) {
            recorder.startRecording();
            recorder.read(audio_data, 0, CHUNK_SIZE_IN_BYTES / 2);
            recorder.stop();
            for (int i = 0; i < CHUNK_SIZE_IN_SAMPLES; i++) {
                data[i * 2] = audio_data[i];
                data[i * 2 + 1] = 0;
            }

            DoFFT(data, CHUNK_SIZE_IN_SAMPLES);

            double best_frequency = min_frequency_fft;
            double best_amplitude = 0;
            HashMap<Double, Double> frequencies = new HashMap<Double, Double>();
            final double draw_frequency_step = 1.0 * RATE
                    / CHUNK_SIZE_IN_SAMPLES;
            for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
                final double current_frequency = i * 1.0 * RATE
                        / CHUNK_SIZE_IN_SAMPLES;
                final double draw_frequency = Math
                        .round((current_frequency - MIN_FREQUENCY)
                                / DRAW_FREQUENCY_STEP)
                        * DRAW_FREQUENCY_STEP + MIN_FREQUENCY;
                final double current_amplitude = Math.pow(data[i * 2], 2)
                        + Math.pow(data[i * 2 + 1], 2);
                final double normalized_amplitude = current_amplitude *
                        Math.pow(MIN_FREQUENCY * MAX_FREQUENCY, 0.5) / current_frequency;
                Double current_sum_for_this_slot = frequencies
                        .get(draw_frequency);
                if (current_sum_for_this_slot == null) {
                    current_sum_for_this_slot = 0.0;
                }
                frequencies.put(draw_frequency, Math
                        .pow(current_amplitude, 0.5)
                        / draw_frequency_step + current_sum_for_this_slot);
                if (normalized_amplitude > best_amplitude) {
                    best_frequency = current_frequency;
                    best_amplitude = normalized_amplitude;
                }
            }
            //PostToUI(frequencies, best_frequency);
            Log.i(LOG_TAG, String.format("Frequencies: %s; Best (?) Frequency: %f",
                    Arrays.toString(frequencies.values().toArray()),
                    best_frequency));
            postToUI(frequencies, best_frequency);
        }

        Log.i(LOG_TAG, "FFT thread closed.");
    }

    private void postToUI(final Map<Double, Double> frequencies, final double pitch) {
        handler.post(() -> parent.updateUI(frequencies, pitch));
    }
}

/* Copyright (C) 2009 by Aleksey Surkov.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted, provided
 * that the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation.  This software is provided "as is" without express or
 * implied warranty.
 */
package cosc345.app.lib;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import cosc345.app.views.fftTest;

/**
 * A class that takes mic input from an Android device and uses a variation of
 * the Fast Fourier Transformation to convert that input into a frequency.
 * <p>
 * Code adapted from https://github.com/eresid/android-guitar-tuner
 */
public class FFT implements Runnable {
    private final static int RATE = 8000;
    private final static int CHANNEL_MODE = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final static int BUFFER_SIZE_IN_MS = 3000;
    private final static int CHUNK_SIZE_IN_SAMPLES = 4096; // = 2 ^
    // CHUNK_SIZE_IN_SAMPLES_POW2
    private final static int CHUNK_SIZE_IN_MS = 1000 * FFT.CHUNK_SIZE_IN_SAMPLES
            / FFT.RATE;
    private final static int BUFFER_SIZE_IN_BYTES = FFT.RATE * FFT.BUFFER_SIZE_IN_MS
            / 1000 * 2;
    private final static int CHUNK_SIZE_IN_BYTES = FFT.RATE * FFT.CHUNK_SIZE_IN_MS
            / 1000 * 2;
    private final static int MIN_FREQUENCY = 50; // HZ
    private final static int MAX_FREQUENCY = 600; // HZ
    private final static int DRAW_FREQUENCY_STEP = 5;
    private final static int MOVING_AVG_PERIOD = 4;
    private static final String LOG_TAG = "FFT";

    private final fftTest parent;
    private final android.os.Handler handler;
    private volatile double latestFrequency;
    private volatile double avgFrequency;
    private volatile double latestAmplitude;

    /**
     * @param parent  the parent activity - this where GUI output should be sent.
     */
    public FFT(fftTest parent) {
        this.parent = parent;
        handler = new Handler();
        latestFrequency = 0.0;
        latestAmplitude = 0.0;
    }


    // Adapted from http://www.drdobbs.com/cpp/a-simple-and-efficient-fft-implementatio/199500857

    /**
     * Perform the FFT algorithm on the given audio input.
     *
     * @param data the byte buffer containing the audio input.
     * @param nn
     */
    private static void DoFFT(double[] data, int nn) {
        long n;
        long mmax;
        long m;
        long istep;
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

    @Override
    public void run() {
        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, FFT.RATE, FFT.CHANNEL_MODE,
                FFT.ENCODING, 6144);

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(FFT.LOG_TAG, "Can't initialize AudioRecord");
            return;
        } else {
            Log.i(FFT.LOG_TAG, "FFT thread started.");
        }

        short[] audio_data = new short[FFT.BUFFER_SIZE_IN_BYTES / 2];
        double[] data = new double[FFT.CHUNK_SIZE_IN_SAMPLES * 2];
        int min_frequency_fft = Math.round(FFT.MIN_FREQUENCY
                * FFT.CHUNK_SIZE_IN_SAMPLES / FFT.RATE);
        int max_frequency_fft = Math.round(FFT.MAX_FREQUENCY
                * FFT.CHUNK_SIZE_IN_SAMPLES / FFT.RATE);
        double[] freqMovingAvg = new double[FFT.MOVING_AVG_PERIOD];

        while (!Thread.interrupted()) {
            recorder.startRecording();
            recorder.read(audio_data, 0, FFT.CHUNK_SIZE_IN_BYTES / 2);
            recorder.stop();

            for (int i = 0; i < FFT.CHUNK_SIZE_IN_SAMPLES; i++) {
                data[i * 2] = audio_data[i];
                data[i * 2 + 1] = 0;
            }

            FFT.DoFFT(data, FFT.CHUNK_SIZE_IN_SAMPLES);

            double bestFrequency = min_frequency_fft;
            double bestAmplitude = 0;
            HashMap<Double, Double> frequencies = new HashMap<>();
            double draw_frequency_step = 1.0 * FFT.RATE
                    / FFT.CHUNK_SIZE_IN_SAMPLES;

            for (int i = min_frequency_fft; i <= max_frequency_fft; i++) {
                double current_frequency = i * 1.0 * FFT.RATE
                        / FFT.CHUNK_SIZE_IN_SAMPLES;
                double draw_frequency = Math
                        .round((current_frequency - FFT.MIN_FREQUENCY)
                                / FFT.DRAW_FREQUENCY_STEP)
                        * FFT.DRAW_FREQUENCY_STEP + FFT.MIN_FREQUENCY;
                double current_amplitude = Math.pow(data[i * 2], 2)
                        + Math.pow(data[i * 2 + 1], 2);
                double normalized_amplitude = current_amplitude *
                        Math.pow(FFT.MIN_FREQUENCY * FFT.MAX_FREQUENCY, 0.5) / current_frequency;
                Double current_sum_for_this_slot = frequencies
                        .get(draw_frequency);

                if (current_sum_for_this_slot == null) {
                    current_sum_for_this_slot = 0.0;
                }

                frequencies.put(draw_frequency, Math
                        .pow(current_amplitude, 0.5)
                        / draw_frequency_step + current_sum_for_this_slot);

                if (normalized_amplitude > bestAmplitude) {
                    bestFrequency = current_frequency;
                    bestAmplitude = normalized_amplitude;
                }
            }

            double avg = 0;

            for (int i = 0; i < freqMovingAvg.length; i++) {
                if (i == freqMovingAvg.length - 1) {
                    freqMovingAvg[freqMovingAvg.length - 1] = bestFrequency;
                } else {
                    freqMovingAvg[i] = freqMovingAvg[i + 1];
                }

                avg += freqMovingAvg[i];
            }

            avg /= freqMovingAvg.length;


            Log.i(FFT.LOG_TAG + "/Output",
                    String.format("Best Frequency: %.2f; Avg Frequency %.2f; Best Amplitude: %.2f; Frequencies: %s",
                            bestFrequency, avgFrequency, bestAmplitude,
                            Arrays.toString(frequencies.values().toArray())));
            avgFrequency = avg;
            latestFrequency = bestFrequency;
            latestAmplitude = bestAmplitude;

            handler.post(() -> parent.updateUI(latestFrequency, avgFrequency, latestAmplitude));
        }

        recorder.release();
        Log.i(FFT.LOG_TAG, "FFT thread closed.");
    }

    /**
     * Get the most recent frequency (Hz) reading.
     * @return the most recent frequency reading.
     */
    public double getHertz() {
        return latestFrequency;
    }

    /**
     * Get the most recent amplitude reading.
     * @return the most recent amplitude reading.
     */
    public double getAmplitude() {
        return latestAmplitude;
    }
}

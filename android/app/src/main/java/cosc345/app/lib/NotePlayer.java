package cosc345.app.lib;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

/**
 * Takes a note or frequency and plays it.
 */
public class NotePlayer implements Runnable {
    // Code adapted from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    private static final String LOG_TAG = "NotePlayer";
    private static final int SAMPLE_RATE = 8000;
    private final int duration; // seconds
    private final int numSamples;
    private final double sample[];
    private final byte generatedSnd[];
    private double frequency;
    private Handler handler = new Handler();

    /**
     * Create a new note player.
     * @param frequency the frequency of the tone to play.
     * @param duration how long to play the tone (in seconds).
     */
    public NotePlayer(double frequency, int duration) {
        this.frequency = frequency;
        this.duration = duration;
        this.numSamples = SAMPLE_RATE * duration;
        this.sample = new double[numSamples];
        this.generatedSnd = new byte[2 * numSamples];
    }

    /**
     * Create a new note player.
     * @param note the note to play.
     * @param duration how long to play the note (in seconds).
     */
    public NotePlayer(Note note, int duration) {
        this(note.getFrequency(), duration);
    }

    /**
     * Generate and play the tone.
     */
    public void run() {
        genTone();
        handler.post(this::play);
    }

    /**
     * Generate the tone to play.
     */
    private void genTone() {
        Log.i(LOG_TAG, "Generating tone.");
        // fill out the array
        double coefficient = 2 * Math.PI / (SAMPLE_RATE / frequency);

        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(coefficient * i);
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    /**
     * Play the tone.
     */
    private void play() {
        Log.i(LOG_TAG, String.format("Playing note with a frequency of %.2f for %d seconds",
                frequency, duration));
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.setNotificationMarkerPosition(numSamples);
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.i(LOG_TAG, "Playback finished.");
                audioTrack.release();
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
            }
        });

        audioTrack.play();
    }

}

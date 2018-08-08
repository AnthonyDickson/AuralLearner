package cosc345.app.model;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import cosc345.app.lib.Callback;
import cosc345.app.lib.Note;

/**
 * Takes a note or frequency and plays it.
 */
public class PlayableNote extends Note implements Runnable {
    // Code adapted from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    private static final String LOG_TAG = "PlayableNote";
    private static final int SAMPLE_RATE = 8000; // per second.
    private final int numSamples;
    private final double sample[];
    private final byte generatedSnd[];
    private final Handler handler = new Handler();
    public Callback callback = null;
    private AudioTrack audioTrack;

    public PlayableNote(double frequency, NoteLength noteLength, boolean useDottedLength) {
        super(frequency, noteLength, useDottedLength);

        numSamples = PlayableNote.SAMPLE_RATE * duration / 1000;
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];
    }

    public PlayableNote(String name, NoteLength noteLength, boolean useDottedLength) {
        super(name, noteLength, useDottedLength);

        numSamples = PlayableNote.SAMPLE_RATE * duration / 1000;
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];
    }

    public PlayableNote(Note note) {
        super(note);

        numSamples = PlayableNote.SAMPLE_RATE * duration / 1000;
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];
    }

    /**
     * Generate and play the tone.
     */
    @Override
    public void run() {
        genTone();
        handler.post(this::play);
    }

    /**
     * Generate the tone to play.
     */
    private void genTone() {
        Log.i(PlayableNote.LOG_TAG, "Generating tone.");
        // fill out the array
        double coefficient = 2 * Math.PI / (PlayableNote.SAMPLE_RATE / frequency);

        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(coefficient * i);
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (double dVal : sample) {
            // scale to maximum amplitude
            short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    /**
     * Play the tone.
     */
    private void play() {
        Log.i(PlayableNote.LOG_TAG, String.format("Playing note with a frequency of %.2f for %d ms",
                frequency, duration));
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                PlayableNote.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.setNotificationMarkerPosition(numSamples);
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.i(PlayableNote.LOG_TAG, "Playback finished.");
                audioTrack.release();
                if (callback != null) {
                    callback.execute();
                }
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
            }
        });

        audioTrack.play();
    }

    public synchronized void stop() {
        if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            audioTrack.flush();
            audioTrack.release();

        }

        if (callback != null) {
            callback.execute();
        }
    }

}

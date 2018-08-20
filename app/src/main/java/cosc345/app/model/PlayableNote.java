package cosc345.app.model;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import cosc345.app.lib.Callback;
import cosc345.app.lib.Note;
import cosc345.app.lib.Playable;

/**
 * Extends the note class such that a note can be played back as audio.
 */
public class PlayableNote extends Note implements Playable, AudioTrack.OnPlaybackPositionUpdateListener {
    private static final String LOG_TAG = "PlayableNote";
    private static final int SAMPLE_RATE = 8000; // per second.

    private AudioTrack audioTrack;
    private byte generatedSnd[];
    private Callback callback = null;
    private int numSamples;
    private Thread thread;

    /**
     * Create a musical note based on a frequency.
     *
     * @param frequency       the frequency (in Hertz) to use.
     * @param noteLength      the length of the note (e.g. crotchet).
     * @param useDottedLength whether or not the note length is dotted or not.
     */
    public PlayableNote(double frequency, NoteLength noteLength, boolean useDottedLength) {
        super(frequency, noteLength, useDottedLength);

        generateTone();
    }

    public PlayableNote(String name) {
        this(name, NoteLength.CROTCHET, false);
    }

    /**
     * Create a musical note from a string.
     *
     * @param name the name of the note that follows the format (Note Letter)[#|b](Octave).
     *             For example a note name may look like: A#3 or Db4.
     * @param noteLength      the length of the note (e.g. crotchet).
     * @param useDottedLength whether or not the note length is dotted or not.
     */
    public PlayableNote(String name, NoteLength noteLength, boolean useDottedLength) {
        super(name, noteLength, useDottedLength);

        generateTone();
    }

    /**
     * Create a playable note where the note is copy of another Note object.
     *
     * @param note the Note object to be copied.
     */
    public PlayableNote(Note note) {
        super(note);

        generateTone();
    }

    /**
     * Generate the tone of
     */
    private void generateTone() {
        // Code adapted from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
        numSamples = PlayableNote.SAMPLE_RATE * duration / 1000;
        generatedSnd = new byte[2 * numSamples];

        Log.i(PlayableNote.LOG_TAG, "Generating tone.");
        double[] sample = new double[numSamples];

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

    @Override
    public void setNoteLength(NoteLength noteLength) {
        this.setNoteLength(noteLength, false);
    }

    @Override
    public void setNoteLength(NoteLength noteLength, boolean useDottedLength) {
        super.setNoteLength(noteLength, useDottedLength);

        generateTone();
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void play() {
        thread = new Thread(() -> {
            Log.i(PlayableNote.LOG_TAG, String.format("Playing note with a frequency of %.2f for %d ms",
                    frequency, duration));
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    PlayableNote.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                    AudioTrack.MODE_STATIC);
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
            audioTrack.setNotificationMarkerPosition(numSamples);
            audioTrack.setPlaybackPositionUpdateListener(this);
            audioTrack.play();
        });

        thread.start();
    }

    @Override
    public void onMarkerReached(AudioTrack track) {
        Log.i(PlayableNote.LOG_TAG, "Playback finished.");
        audioTrack.release();

        if (callback != null) {
            callback.execute();
        } else {
            Log.i(LOG_TAG, "Callback is null!");
        }
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {
    }

    @Override
    public void stop() {
        if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            audioTrack.flush();
            audioTrack.release();
        }

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        if (callback != null) {
            callback.execute();
        }
    }

}

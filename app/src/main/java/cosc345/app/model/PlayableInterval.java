package cosc345.app.model;

import android.util.Log;

import cosc345.app.model.Callback;
import cosc345.app.model.Interval;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
import cosc345.app.model.State;

/**
 * Extends the interval class such that a interval can be played back as audio.
 */
public class PlayableInterval extends Interval implements Playable {
    private static final String LOG_TAG = "PlayableInterval";

    private State state;
    private Callback callback;

    public final PlayableNote root;
    public final PlayableNote other;

    /**
     * Create an interval from a single note.
     *
     * @param root     the root note of the interval.
     * @param interval the interval to create.
     */
    public PlayableInterval(Note root, Intervals interval) {
        this(root, interval, false);
    }

    /**
     * Create an interval from a single note.
     *
     * @param root     the root note of the interval.
     * @param interval the interval to create.
     * @param invert   inverts the
     */
    public PlayableInterval(Note root, Intervals interval, boolean invert) {
        super(root, interval, invert);

        this.root = new PlayableNote(super.root);
        this.other = new PlayableNote(super.other);
        this.state = State.READY;
    }

    /**
     * Create an interval from two notes.
     */
    public PlayableInterval(Note root, Note other) {
        super(root, other);

        this.root = new PlayableNote(super.root);
        this.other = new PlayableNote(super.other);
        this.state = State.READY;
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void play() {
        if (state != State.READY) return;

        Log.i(LOG_TAG, "Playing interval.");
        root.setCallback(this::playNext);
        other.setCallback(this::onDone);
        root.play();
        state = State.BUSY;
    }

    @Override
    public void stop() {
        if (state != State.BUSY) return;

        Log.i(LOG_TAG, "Stopping interval playback.");
        state = State.PAUSED;
        root.stop();
        other.stop();

        onDone();
    }

    /**
     * Play the next note in the interval.
     */
    private void playNext() {
        if (state != State.BUSY) return;

        Log.i(LOG_TAG, "Playing next note.");
        other.play();
    }

    /**
     * Handle the playback finishing.
     */
    private void onDone() {
        state = State.READY;
        Log.i(LOG_TAG, "Interval playback finished.");

        if (callback != null) {
            callback.execute();
        }
    }
}

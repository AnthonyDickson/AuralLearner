package cosc345.app.model;

/**
 * Defines the common methods available to an object that is playable, e.g. a
 * note, an interval, a melody etc.
 *
 * Objects interested in when a Playable object starts or finishes playback
 * should implement the Delegate interface.
 *
 * @see Delegate
 */
public abstract class Playable {
    protected Delegate delegate;
    protected boolean isPlaying;

    public Playable() {
        this.isPlaying = false;
    }

    /**
     * Set the Delegate for the Playable object.
     *
     * @see Delegate
     * @param delegate the delegate to be used.
     */
    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Play the Playable object and notify the delegate that playback has started.
     */
    public void play() {
        isPlaying = true;

        if (delegate != null) {
            delegate.onPlaybackStarted();
        }
    }

    /**
     * Stop the playback of the Playable object and call onDone().
     */
    public void stop() {
        onDone();
    }

    /**
     * Handle any cleanup that needs to be done after playback is finished and notify the delegate
     * that playback has finished.
     *
     * This should be called when the Playable object has finished playback or the Playable object
     * was stopped.
     */
    protected void onDone() {
        isPlaying = false;

        if (delegate != null) {
            delegate.onDone();
        }
    }

    /**
     * Delegate provides an interface for objects playing a playable to control what happens
     * when a playable starts and finishes playback.
     */
    public interface Delegate {
        /** Handle anything that should be done after playback starts. */
        void onPlaybackStarted();
        /** Handle anything that should be done after playback successfully finishes. */
        void onPlaybackFinished();
        /** Handle cleanup that should be done after playback finishes, regardless if playback finished
         * uninterrupted (i.e. stop() was called) or not. */
        void onDone();
    }
}

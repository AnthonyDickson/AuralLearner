package cosc345.app.model;

/**
 * Defines the common methods available to an object that is playable, e.g. a
 * note, an interval, a melody etc.
 *
 * Objects interested in when a Playable object starts or finishes playback
 * should implement the PlayableDelegate interface.
 *
 * @see PlayableDelegate
 */
public abstract class Playable {
    protected PlayableDelegate delegate;
    protected boolean isPlaying;

    public Playable() {
        this.isPlaying = false;
    }

    /**
     * Set the PlayableDelegate for the Playable object.
     *
     * @see PlayableDelegate
     * @param delegate the delegate to be used.
     */
    public void setDelegate(PlayableDelegate delegate) {
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
            delegate.onPlaybackFinished();
        }
    }

    /**
     * PlayableDelegate provides an interface for objects playing a playable to control what happens
     * when a playable starts & finishes playback.
     */
    public interface PlayableDelegate {
        void onPlaybackStarted();
        void onPlaybackFinished();
    }
}

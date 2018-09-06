package cosc345.app.model;

/**
 * Defines the common methods available to an object that is playable, e.g. a
 * note, an interval, a melody etc.
 */
public interface Playable {
    /**
     * Set the callback to be executed when the item either:
     *  - finishes playback
     *  - is stopped.
     *
     *  @param callback the callback to be executed.
     */
    void setCallback(Callback callback);

    /**
     * Play the item and when the playback finishes call the assigned callback function.
     */
    void play();

    /**
     * Stop the playback of the item and execute the assigned callback.
     */
    void stop();
}

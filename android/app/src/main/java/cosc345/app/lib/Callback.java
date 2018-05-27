package cosc345.app.lib;

/**
 * A simple interface for a callback method with no arguments.
 */
@FunctionalInterface
public interface Callback {
    /**
     * Execute the callback.
     */
    void execute();
}

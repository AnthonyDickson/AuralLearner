package cosc345.AuralLearner.model;

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

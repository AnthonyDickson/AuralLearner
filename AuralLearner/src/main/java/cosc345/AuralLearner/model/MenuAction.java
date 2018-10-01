package cosc345.AuralLearner.model;

/**
 * Representation of an action executable from the voice control menu.
 */
public class MenuAction {
    public final String activation;
    private final Callback action;

    /**
     * @param activation the keyword/keyphrase that this action will be
     *                   activated by.
     * @param action the action (or method) to execute.
     */
    public MenuAction(String activation, Callback action) {
        this.activation = activation;
        this.action = action;
    }

    /**
     * Execute this MenuAction's action.
     */
    public void execute() {
        if (action != null) {
            action.execute();
        }
    }
}

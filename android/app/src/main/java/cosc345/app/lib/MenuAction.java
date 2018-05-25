package cosc345.app.lib;

/**
 * Representation of an action executable from the voice control menu.
 */
public class MenuAction {
    final String activation;
    private Callback action;

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
        action.execute();
    }
}

package cosc345.app.lib;

public class MenuAction {
    final String activation;
    private Callback callbackInterface;

    public MenuAction(String activation, Callback callbackInterface) {
        this.activation = activation;
        this.callbackInterface = callbackInterface;
    }

    public void execute() {
        callbackInterface.execute();
    }
}

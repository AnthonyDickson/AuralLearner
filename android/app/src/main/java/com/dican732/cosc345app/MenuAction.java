package com.dican732.cosc345app;

class MenuAction {
    String activation;
    MenuActionInterface menuActionInterface;

    public MenuAction(String activation, MenuActionInterface menuActionInterface) {
        this.activation = activation;
        this.menuActionInterface = menuActionInterface;
    }

    public void execute() {
        menuActionInterface.execute();
    }
}

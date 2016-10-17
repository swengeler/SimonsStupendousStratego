package project.stratego.control;

public class ManagerManager {

    private static ModelComManager modelReceiver;
    private static ViewComManager viewReceiver;

    public static void configureSinglePlayer() {
        //modelReceiver = CombinedComManager.getInstance();
        //viewReceiver = CombinedComManager.getInstance();
    }

    public static void configureMultiPlayer() {
        modelReceiver = ModelComManager.getInstance();
        viewReceiver = ViewComManager.getInstance();
    }

    public static ModelComManager getModelReceiver() {
        return modelReceiver;
    }

    public static ViewComManager getViewReceiver() {
        return viewReceiver;
    }

}

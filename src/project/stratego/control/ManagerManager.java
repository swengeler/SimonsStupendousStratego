package project.stratego.control;

public class ManagerManager {

    private static ModelReceiver modelReceiver;
    private static ViewReceiver viewReceiver;

    public static void configureSinglePlayer() {
        modelReceiver = CombinedComManager.getInstance();
        viewReceiver = CombinedComManager.getInstance();
    }

    public static void configureMultiPlayer() {
        modelReceiver = ModelComManager.getInstance();
        viewReceiver = ViewComManager.getInstance();
    }

    public static ModelReceiver getModelReceiver() {
        return modelReceiver;
    }

    public static ViewReceiver getViewReceiver() {
        return viewReceiver;
    }

}

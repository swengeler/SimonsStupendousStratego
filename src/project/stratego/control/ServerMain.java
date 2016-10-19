package project.stratego.control;

/**
 *
 */
public class ServerMain {

    public static void main(String[] args) {
        StrategoServer server = new StrategoServer();
        ManagerManager.configureMultiPlayer();
        (ModelComManager.getInstance()).setStrategoServer(server);
        ModelComManager.getInstance().configureMultiPlayer();
        server.launch();
    }

}

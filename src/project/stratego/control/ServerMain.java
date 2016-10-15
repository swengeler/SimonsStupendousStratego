package project.stratego.control;

/**
 *
 */
public class ServerMain {

    public static void main(String[] args) {
        StrategoServer server = new StrategoServer();
        (ModelComManager.getInstance()).setStrategoServer(server);
        server.launch();
    }

}

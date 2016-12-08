package project.stratego.control.server;

import project.stratego.control.managers.ModelComManager;

public class ServerMain {

    public static void main(String[] args) {
        StrategoServer server = new StrategoServer();
        ModelComManager.getInstance().configureMultiPlayer();
        ModelComManager.getInstance().setStrategoServer(server);
        server.launch();
    }

}

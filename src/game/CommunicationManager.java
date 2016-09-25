package game;

import ui.StrategoFrame;

/**
 *
 */
public class CommunicationManager {

    private StrategoFrame strategoFrame;
    private StrategoGame strategoGame;

    public void setStrategoFrame(StrategoFrame strategoFrame) {
        this.strategoFrame = strategoFrame;
    }

    public void setStrategoGame(StrategoGame strategoGame) {
        this.strategoGame = strategoGame;
    }

    public void removeStrategoFrame() {
        strategoFrame = null;
    }

    public void removeStrategoGame() {
        strategoGame = null;
    }

    public boolean isReady() {
        return ((strategoFrame != null) && (strategoGame != null));
    }

    /* View to model methods */

    public void sendPlayerSwitch() {

    }

    /* Model to view methods */

}

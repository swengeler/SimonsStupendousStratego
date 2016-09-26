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

    public void sendTrayPieceSelected(int index) {
        System.out.println("Index = " + index);
        strategoGame.getCurrentState().processTraySelect(PieceType.values()[index]);
    }

    /* Model to view methods */

    public void sendTrayActiveUpdate(PieceType type) {
        // send command to highlight a certain tray icon
    }

    public void sendActivePieceUpdate(Piece activePiece) {
        // send command to board Piece with the same ID, eyoo, was geht da
    }

}

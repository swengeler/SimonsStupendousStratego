package project.stratego.game;

import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

public abstract class GameState {

    protected StrategoGame parent;

    protected Player firstPlayer, secondPlayer;
    protected Player currentPlayer, currentOpponent;
    protected Piece currentPiece;

    public GameState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        this.parent = parent;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    /**
     * Method for processing the selection of a piece to be deployed on the board from the
     * "tray" from which pieces that are not in the game already can be selected.
     */
    public void processTraySelect(PlayerType playerType, PieceType pieceType) {}
    
    /**
     * Method for processing both the selection of a piece and the selection of a position to
     * move a previously selected piece.
     */
    public void processBoardSelect(int row, int col) {}

    public void processPlayerReady() {}

    public void processResetGame() {

    }

}

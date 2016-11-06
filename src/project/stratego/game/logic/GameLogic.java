package project.stratego.game.logic;

import project.stratego.game.StrategoGame;
import project.stratego.game.entities.Piece;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

public abstract class GameLogic {

    protected StrategoGame parent;

    protected Player playerNorth, playerSouth;
    protected Piece currentPiece;

    public GameLogic(StrategoGame parent, Player playerNorth, Player playerSouth) {
        this.parent = parent;
        this.playerNorth = playerNorth;
        this.playerSouth = playerSouth;
    }

    /**
     * Method for processing the selection of a piece to be deployed on the board from the
     * "tray" from which pieces that are not in the game already can be selected.
     */
    public void processTraySelect(int playerIndex, int pieceIndex) {}
    
    /**
     * Method for processing both the selection of a piece and the selection of a position to
     * move a previously selected piece.
     */
    public void processBoardSelect(int playerIndex, int row, int col) {}

    public void processPlayerReady(int playerIndex) {}

}

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

    public void processTraySelect(PlayerType playerType, PieceType pieceType) {}

    public void processBoardSelect(int row, int col) {}

    public void processPlayerReady() {}

}

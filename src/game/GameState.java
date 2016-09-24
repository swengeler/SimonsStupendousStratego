package game;

/**
 *
 */
public abstract class GameState {

    protected StrategoGame parent;

    protected Player firstPlayer, secondPlayer;
    protected Piece activePiece;

    public GameState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        this.parent = parent;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public void processTraySelect(/*Piece or PieceType*/) {}

    public void processBoardSelect(int row, int col) {}

    public void processPlayerReady() {}

    public void processSwitch() {}

    public void processInputAction(InputAction a) {}

}

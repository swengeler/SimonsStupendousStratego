package project.stratego.game.entities;

import project.stratego.ai.AIMove;
import project.stratego.game.utils.Move;
import project.stratego.game.utils.MoveManager;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

public class GameState {

    public static final int BOARD_SIZE = 10;

    protected BoardTile[][] board;
    protected Player playerNorth, playerSouth;

    public GameState() {
        boardSetup();
    }

    private GameState(BoardTile[][] board, Player playerNorth, Player playerSouth) {
        this.board = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                this.board[row][col] = board[row][col].clone();
            }
        }
        this.playerNorth = playerNorth.clone();
        this.playerSouth = playerSouth.clone();
    }

    private GameState(BoardTile[][] board, Player playerNorth, Player playerSouth, int playerIndex) {
        int opponentIndex = playerIndex == 0 ? 1 : 0;
        this.board = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                this.board[row][col] = board[row][col].clone();
                if (this.board[row][col].getOccupyingPiece().getPlayerType() == PlayerType.values()[opponentIndex]) {
                    this.board[row][col].getOccupyingPiece().setType(PieceType.UNKNOWN);
                }
            }
        }
        this.playerNorth = playerNorth.clone();
        this.playerSouth = playerSouth.clone();
    }

    private void boardSetup() {
        // creating the board array, setting the right accessibility of board tiles
        board = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        // change this to be more efficient loop
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row == 4 || row == 5) && (col == 2 || col == 3 || col == 6 || col == 7)) {
                    board[row][col] = new BoardTile(false, row, col);
                } else {
                    board[row][col] = new BoardTile(true, row, col);
                }
            }
        }

        // creating and assigning all the pieces to each of the two players
        playerNorth = new Player(PlayerType.NORTH);
        playerSouth = new Player(PlayerType.SOUTH);
    }

    public BoardTile[][] getBoardArray() {
        return board;
    }

    public Player getPlayerNorth() {
        return playerNorth;
    }

    public Player getPlayerSouth() {
        return playerSouth;
    }

    public Player getPlayer(int index) {
        return index == 0 ? playerNorth : playerSouth;
    }

    public void applyMove(Move move) {
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        if (movingPiece == null) {
            return;
        }
        MoveManager moveManager = new MoveManager(board);
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());
    }

    /* Clone methods */

    public GameState clone() {
        GameState clone = new GameState(board, playerNorth, playerSouth);
        return clone;
    }

    public GameState clone(int playerIndex) {
        GameState clone = new GameState(board, playerNorth, playerSouth, playerIndex);
        return clone;
    }

}

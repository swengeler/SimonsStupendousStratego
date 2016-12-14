package project.stratego.game.entities;

import project.stratego.game.moves.*;
import project.stratego.game.utils.*;

import java.util.LinkedList;

public class GameState {

    public static final int BOARD_SIZE = 10;

    protected BoardTile[][] board;
    protected Player playerNorth, playerSouth;

    protected LinkedList<Move> moveHistory;

    public GameState() {
        boardSetup();
        moveHistory = new LinkedList<>();
    }

    protected GameState(BoardTile[][] board, Player playerNorth, Player playerSouth, LinkedList<Move> moveHistory) {
        this.playerNorth = new Player(playerNorth.getType());
        this.playerSouth = new Player(playerSouth.getType());

        this.board = new BoardTile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                this.board[row][col] = board[row][col].clone();
                if (this.board[row][col].getOccupyingPiece() != null && this.board[row][col].getOccupyingPiece().getPlayerType() == PlayerType.NORTH) {
                    this.playerNorth.getActivePieces().add(this.board[row][col].getOccupyingPiece());
                } else if (this.board[row][col].getOccupyingPiece() != null) {
                    this.playerSouth.getActivePieces().add(this.board[row][col].getOccupyingPiece());
                }
            }
        }

        this.moveHistory = new LinkedList<>();
        for (Move m : moveHistory) {
            this.moveHistory.add(m.clone());
        }
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

    public Player getPlayer(PlayerType type) {
        return type == playerNorth.getType() ? playerNorth : playerSouth;
    }

    public LinkedList<Move> getMoveHistory() {
        return moveHistory;
    }

    public void applyMove(Move move) {
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        if (movingPiece == null) {
            return;
        }
        MoveManager moveManager = new DiscreteMoveManager(board);
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());
    }

    public void copySetup(GameState state, int playerIndex) {
        //System.out.println("Setup should be copied to gamestate from this board:");
        //state.printBoard();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col] = state.getBoardArray()[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].clone();
                if (board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece() != null) {
                    getPlayer(playerIndex).getActivePieces().add(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
                }
            }
        }
    }

    /* Clone methods */

    public GameState clone() {
        GameState clone = new GameState(board, playerNorth, playerSouth, moveHistory);
        return clone;
    }

    public GameState clone(int playerIndex) {
        GameState clone = new GameState(board, playerNorth, playerSouth, playerIndex);
        return clone;
    }

    public void printBoard() {
        System.out.println("\nBOARD:");
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print("-------------------------------------------------------------------------------------------\n|");
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].isAccessible()) {
                    System.out.print("   ~~   |");
                } else if (board[row][col].getOccupyingPiece() == null) {
                    System.out.print("        |");
                } else {
                    System.out.print(" " + board[row][col].getOccupyingPiece().getType().toString().substring(0, 2) + " (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |");
                }
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------------------------------------------------------\n");
    }

}

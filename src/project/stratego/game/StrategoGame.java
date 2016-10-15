package project.stratego.game;

import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.MoveManager;
import project.stratego.game.utils.PlayerType;

public class StrategoGame {

    public static final int BOARD_SIZE = 10;

    private final int gameID;

    private GameLogic currentState;
    private MoveManager moveManager;
    private BoardTile[][] board;

    private Player playerNorth, playerSouth;

    public StrategoGame(int gameID) {
        this.gameID = gameID;
        boardSetup();
        componentSetup();
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

    private void componentSetup() {
        currentState = new DeploymentLogic(this, playerNorth, playerSouth);
        moveManager = new MoveManager(board);
    }

    /* Getter methods */

    public int getGameID() {
        return gameID;
    }

    public GameLogic getCurrentState() {
        return currentState;
    }

    public MoveManager getMoveManager() {
        return moveManager;
    }

    public BoardTile[][] getBoard() {
        return board;
    }

    public BoardTile[][] getBoardClone() {
        BoardTile[][] clone = new BoardTile[board.length][board[0].length];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                clone[row][col] = board[row][col].clone();
            }
        }
        return clone;
    }

    public void switchStates() {
        System.out.println("States switches");
        System.out.println("playerNorth has " + playerNorth.getActivePieces().size() + " pieces");
        currentState = currentState instanceof DeploymentLogic ? new PlayingLogic(this, playerNorth, playerSouth) : new DeploymentLogic(this, playerNorth, playerSouth);
    }

    public void resetGame() {
        boardSetup();
        currentState = new DeploymentLogic(this,playerNorth, playerSouth);
    }

}

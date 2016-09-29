package project.stratego.game;

import project.stratego.control.CommunicationManager;
import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Player;
import project.stratego.game.utils.MoveManager;
import project.stratego.game.utils.PlayerType;

public class StrategoGame {

    public static final int BOARD_SIZE = 10;

    private static StrategoGame instance;
    private static boolean initialised;

    private GameState currentState;
    private MoveManager moveManager;
    private CommunicationManager controller;
    private BoardTile[][] board;

    private Player playerNorth, playerSouth;

    public static StrategoGame getInstance() {
        return instance;
    }

    public static void initialise(CommunicationManager controller) {
        if (!initialised) {
            instance = new StrategoGame(controller);
            initialised = true;
        }
    }

    private StrategoGame(CommunicationManager controller) {
        boardSetup();
        componentSetup(controller);
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

    private void componentSetup(CommunicationManager controller) {
        currentState = new DeploymentState(this, playerNorth, playerSouth);
        moveManager = new MoveManager(board);
        this.controller = controller;
        controller.setStrategoGame(this);
    }

    /* Getter methods */

    public CommunicationManager getComManager() {
        return controller;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public MoveManager getMoveManager() {
        return moveManager;
    }

    public BoardTile[][] getBoard() {
        return board;
    }

    public BoardTile[][] getCloneBoard() {
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
        currentState = currentState instanceof DeploymentState ? new PlayingState(this, playerNorth, playerSouth) : new DeploymentState(this, playerNorth, playerSouth);
    }

}

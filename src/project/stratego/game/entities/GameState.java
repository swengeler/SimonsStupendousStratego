package project.stratego.game.entities;

import project.stratego.game.moves.*;
import project.stratego.game.utils.*;

import java.util.LinkedList;

public class GameState {

    public static final int BOARD_SIZE = 10;

    protected BoardTile[][] board;
    protected Player playerNorth, playerSouth;

    protected String initBoardEncoding;
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
                    //this.playerNorth.getHiddenPieces().add(this.board[row][col].getOccupyingPiece());
                } else if (this.board[row][col].getOccupyingPiece() != null) {
                    this.playerSouth.getActivePieces().add(this.board[row][col].getOccupyingPiece());
                    //this.playerSouth.getHiddenPieces().add(this.board[row][col].getOccupyingPiece());
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

    private void boardSetup(int playerIndex) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[playerIndex == PlayerType.NORTH.ordinal() ? row : 9 - row][col] = new BoardTile(true, playerIndex == PlayerType.NORTH.ordinal() ? row : 9 - row, col);
            }
        }
        if (playerIndex == PlayerType.NORTH.ordinal()) {
            playerNorth = new Player(PlayerType.NORTH);
        } else {
            playerSouth = new Player(PlayerType.SOUTH);
        }
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

    public String getSetupEncoding(int playerIndex) {
        if (initBoardEncoding != null && !initBoardEncoding.equals("")) {
            return initBoardEncoding.split("_")[playerIndex];
        }
        String setup = "";
        setup += playerIndex == PlayerType.NORTH.ordinal() ? board[3][9].getOccupyingPiece().getType().ordinal() : board[6][0].getOccupyingPiece().getType().ordinal();
        for (int i = 1; i < 40; i++) {
            setup += "-" + board[playerIndex == PlayerType.NORTH.ordinal() ? 3 - i / 10 : 6 + i / 10][playerIndex == PlayerType.NORTH.ordinal() ? 9 - i % 10 : i % 10].getOccupyingPiece().getType().ordinal();
        }
        return setup;
    }

    public String getInitBoardEncoding() {
        return initBoardEncoding;
    }

    public String getMoveEncoding() {
        if (moveHistory.isEmpty()) {
            return "";
        }
        String moveEncoding = "";
        for (Move m : moveHistory) {
            moveEncoding += "_" + m.getPlayerIndex() + "-" + m.getOrRow() + "-" + m.getOrCol() + "-" + m.getDestRow() + "-" + m.getDestCol();
        }
        moveEncoding = moveEncoding.substring(1, moveEncoding.length());
        return moveEncoding;
    }

    public void applyMove(Move move) {
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        if (movingPiece == null) {
            return;
        }
        MoveManager moveManager = new DiscreteMoveManager(board);
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());
        if (moveManager.lastMoveResult() != MoveResult.NOMOVE) {
            moveHistory.add(move);
        }
    }

    public void undoLastMove() {
        Move lastMove = moveHistory.pop();
        if (board[lastMove.getDestRow()][lastMove.getDestCol()].getOccupyingPiece() == null) {
            // pieces eliminated each other
        } else if (lastMove.getPlayerIndex() == board[lastMove.getDestRow()][lastMove.getDestCol()].getOccupyingPiece().getType().ordinal()) {
            // attacking piece won or piece just moved to this square
        } else if (lastMove.getPlayerIndex() != board[lastMove.getDestRow()][lastMove.getDestCol()].getOccupyingPiece().getType().ordinal()) {
            // attacking piece lost
        }
    }

    public void saveInitBoard() {
        if (initBoardEncoding != null) {
            return;
        }
        initBoardEncoding = "";
        // setup north
        initBoardEncoding += board[3][9].getOccupyingPiece().getType().ordinal();
        for (int i = 1; i < 40; i++) {
            initBoardEncoding += "-" + board[3 - i / 10][9 - i % 10].getOccupyingPiece().getType().ordinal();
        }
        // setup south
        initBoardEncoding += "_" + board[6][0].getOccupyingPiece().getType().ordinal();
        for (int i = 1; i < 40; i++) {
            initBoardEncoding += "-" + board[6 + i / 10][i % 10].getOccupyingPiece().getType().ordinal();
        }
    }

    public void copySetup(GameState state, int playerIndex) {
        //System.out.println("Setup should be copied to gamestate from this board:");
        //state.printBoard();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col] = state.getBoardArray()[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].clone();
                if (board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece() != null) {
                    getPlayer(playerIndex).getActivePieces().add(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
                    //getPlayer(playerIndex).getHiddenPieces().add(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
                }
            }
        }
    }

    public void interpretEncodedSetup(String encodedSetup, int playerIndex) {
        boardSetup(playerIndex);

        String[] pieceIndexStrings = encodedSetup.split("-");
        if (pieceIndexStrings.length != 40) {
            System.out.println("Something wrong with the loaded setup.");
            System.out.println(encodedSetup);
            System.exit(1);
        }
        int[] pieceIndexIntegers = new int[40];
        Piece temp;
        for (int i = 0; i < pieceIndexStrings.length; i++) {
            pieceIndexIntegers[i] = Integer.parseInt(pieceIndexStrings[i]);
            temp = new Piece(PieceType.values()[pieceIndexIntegers[i]], PlayerType.values()[playerIndex]);
            getPlayer(playerIndex).getActivePieces().add(temp);
            board[playerIndex == PlayerType.NORTH.ordinal() ? 3 - i / 10 : 6 + i / 10][playerIndex == PlayerType.NORTH.ordinal() ? 9 - i % 10 : i % 10].setOccupyingPiece(temp);
        }
    }

    /**
     * A method that can be used to load an initial gamestate stored as the players' setups in the form of Strings.
     * The resulting GameState object can then be used for starting a game with those setups, using the move history,
     * which can also be stored with the initial game state, to advance the game to a certain point and continue playing
     * or to go through the moves as a replay of the stored game.
     * @param encodedBoard A String consisting of the encoded setups for each player.
     */
    public void interpretEncodedBoard(String encodedBoard) {
        initBoardEncoding = encodedBoard;

        boardSetup();

        String[] setups = encodedBoard.split("_");
        String[] pieceIndexStrings = setups[0].split("-");
        if (pieceIndexStrings.length != 40) {
            System.out.println("Something wrong with the loaded board.");
            System.exit(1);
        }

        Piece temp;
        int tempPieceIndex;
        for (int i = 0; i < pieceIndexStrings.length; i++) {
            tempPieceIndex = Integer.parseInt(pieceIndexStrings[i]);
            temp = new Piece(PieceType.values()[tempPieceIndex], PlayerType.NORTH);
            playerNorth.getActivePieces().add(temp);
            //playerNorth.getHiddenPieces().add(temp);
            board[3 - i / 10][9 - i % 10].setOccupyingPiece(temp);
        }
        pieceIndexStrings = setups[1].split("-");
        for (int i = 0; i < pieceIndexStrings.length; i++) {
            tempPieceIndex = Integer.parseInt(pieceIndexStrings[i]);
            temp = new Piece(PieceType.values()[tempPieceIndex], PlayerType.SOUTH);
            playerSouth.getActivePieces().add(temp);
            //playerSouth.getHiddenPieces().add(temp);
            board[6 + i / 10][i % 10].setOccupyingPiece(temp);
        }
    }

    /**
     * A method that can be used after loading an initial setup for the game state to advance the game to a certain point
     * (defined by the moves made in said game). Using this method, all moves are applied immediately, meaning that this
     * method cannot be used for the replay functionality in the program. Instead it can serve as a way to load a certain
     * game state that is of some interest. By using an initial game state and then applying moves afterwards it is also
     * ensured that any additional information relevant for AIs is also updated properly.
     * @param encodedMoves A String consisting of encoded moves made in a game.
     */
    public void interpretEncodedMoves(String encodedMoves) {
        // moves encoded as: playerIndex1-orRow1-orCol1-destRow1-destCol1_playerIndex2-orRow2-orCol2-destRow2-destCol2_ ...
        String[] moves = encodedMoves.split("_");
        String[] moveCoords;
        int playerIndex, orRow, orCol, destRow, destCol;
        for (String move : moves) {
            moveCoords = move.split("-");
            playerIndex = Integer.parseInt(moveCoords[0]);
            orRow = Integer.parseInt(moveCoords[1]);
            orCol = Integer.parseInt(moveCoords[2]);
            destRow = Integer.parseInt(moveCoords[3]);
            destCol = Integer.parseInt(moveCoords[4]);
            applyMove(new Move(playerIndex, orRow, orCol, destRow, destCol));
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
        System.out.println("BOARD:\n" + boardToReadableString());
    }

    public String boardToReadableString() {
        String boardString = "";
        for (int row = 0; row < BOARD_SIZE; row++) {
            boardString += "-------------------------------------------------------------------------------------------\n|";
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].isAccessible()) {
                    boardString += "   ~~   |";
                } else if (board[row][col].getOccupyingPiece() == null) {
                    boardString += "        |";
                } else {
                    if (board[row][col].getOccupyingPiece().getType() == PieceType.MAJOR) {
                        boardString += " MJ (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                    } else {
                        boardString += " " + board[row][col].getOccupyingPiece().getType().toString().substring(0, 2) + " (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                    }
                }
            }
            boardString += "\n|";
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].isAccessible()) {
                    boardString += "   ~~   |";
                } else if (board[row][col].getOccupyingPiece() == null) {
                    boardString += "        |";
                } else {
                    boardString += "  (" + (board[row][col].getOccupyingPiece().isRevealed() ? "rv" : (board[row][col].getOccupyingPiece().isMoveRevealed() ? "mr" : "nr")) + ")  |";
                }
            }
            boardString += "\n";
        }
        boardString += "-------------------------------------------------------------------------------------------";
        return boardString;
    }

}

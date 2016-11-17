package project.stratego.ui.sections;

import project.stratego.game.utils.PieceType;
import project.stratego.ui.Messages;
import project.stratego.ui.components.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class InGameView extends Pane {

    public static final int DEFAULT_PLAYER_ID = 0;

    private int playerIndex;

    private BoardArea boardArea;
    private Tray tray;
    private PlayerNames playerNames;

    private Image tileBackground;
    private Image pieceIcons;
    private Image backsidePieceIcons;

    public InGameView(double padding) {
        super();
        loadImages();
        makeComponents();
        placeComponents(padding);
        //setPadding(new Insets(padding / 2));
        setStyle("-fx-background-color: transparent; -fx-padding: 0 20 0 0;");
    }

    private void loadImages() {
        tileBackground = new Image(getClass().getResourceAsStream("/icons/board_background_minimal.png"), 10 * BoardTile.TILE_SIZE, 10 * BoardTile.TILE_SIZE, true, true);
        pieceIcons = new Image(getClass().getResourceAsStream("/icons/piece_icons_outline.png"), 12 * Piece.PIECE_SIZE, Piece.PIECE_SIZE, true, true);
        backsidePieceIcons = new Image(getClass().getResourceAsStream("/icons/backside_piece_icons_outline.png"), 2 * Piece.PIECE_SIZE, Piece.PIECE_SIZE, true, true);
    }

    private void makeComponents() {
        boardArea = new BoardArea(tileBackground, pieceIcons, backsidePieceIcons);
        tray = new Tray(pieceIcons);
        playerNames = new PlayerNames(boardArea.getSize());
        getChildren().addAll(boardArea, tray, playerNames);
    }

    private void placeComponents(double padding) {
        boardArea.setLayoutX(90 + padding);
        boardArea.setLayoutY(10 + 45);

        tray.setLayoutX(0 + padding);
        tray.setLayoutY(0 + 45);

        playerNames.setLayoutX(90 + padding);
    }

    public void processAssignSide(int playerIndex) {
        this.playerIndex = playerIndex;
        tray.setPlayerIndex(playerIndex);
        // possibly do other stuff, e.g. change the color of the tray or smth
        System.out.println("Side assign received in the GUI.");
    }

    public void processHighlightDeployment(boolean highlight) {
        boardArea.highlightDeploymentArea(playerIndex, highlight);
    }

    public void processGameOver(int winnerPlayerIndex) {
        boardArea.revealAll();
        playerNames.resetHighlight();
        if (winnerPlayerIndex == playerIndex) {
            Messages.showPlayerWon();
        } else {
            Messages.showOpponentWon();
        }
        processResetGame();
    }

    public void processChangeTurn(int playerIndex) {
        playerNames.highlightPlayerName(playerIndex);
    }

    public void processPiecePlaced(int playerIndex, int pieceIndex, int row, int col) {
        System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + " (InGameView).");
        /*if (playerIndex == this.playerIndex) {
            tray.setActive(-1);
        }*/
        boardArea.makePiece(playerIndex, pieceIndex, row, col);
    }

    public void processPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        boardArea.movePiece(orRow, orCol, destRow, destCol);
    }

    public void processResetDeployment(int playerIndex) {
        if (playerIndex == -1) {
            boardArea.resetDeployment(DEFAULT_PLAYER_ID);
        } else {
            boardArea.resetDeployment(playerIndex);
        }
    }

    public void processTrayActiveUpdate(int pieceIndex) {
        //tray.setActive(pieceIndex);
    }

    public void processResetGame() {
        processAssignSide(-1);
        boardArea.reset();
        playerNames.resetHighlight();
    }

    public void processHidePiece(int row, int col) {
        boardArea.hidePiece(row, col);
    }

    public void processRevealPiece(int row, int col) {
        boardArea.revealPiece(row, col);
    }

    public void processAttackLost(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.revealPiece(orRow, orCol);
        boardArea.revealPiece(destRow, destCol);
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(stopRow, stopCol);
    }

    public void processAttackTied(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.revealPiece(orRow, orCol);
        boardArea.revealPiece(destRow, destCol);
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(stopRow, stopCol);
        boardArea.removePiece(destRow, destCol);
    }

    public void processAttackWon(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.revealPiece(orRow, orCol);
        boardArea.revealPiece(destRow, destCol);
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(destRow, destCol);
        boardArea.movePiece(stopRow, stopCol, destRow, destCol);
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

}
package project.stratego.ui.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import project.stratego.game.utils.PieceType;
import project.stratego.ui.components.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class InGameView extends Pane {

    public static final int DEFAULT_PLAYER_ID = 0;

    private int playerIndex;

    private BoardArea boardArea;
    private Tray tray;
    private Text northPlayerName, southPlayerName;
    private TextFlow northContainer, southContainer;

    private Image tileBackground;
    private Image pieceIcons;
    private Image backsidePieceIcons;

    public InGameView(double padding) {
        super();
        loadImages();
        makeComponents();
        placeComponents(padding);
        //setPadding(new Insets(padding / 2));
        setStyle("-fx-background-color: transparent;");
    }

    private void loadImages() {
        tileBackground = new Image(getClass().getResourceAsStream("/icons/board_background_minimal.png"), 10 * BoardTile.TILE_SIZE, 10 * BoardTile.TILE_SIZE, true, true);
        pieceIcons = new Image(getClass().getResourceAsStream("/icons/piece_icons_outline.png"), 12 * Piece.PIECE_SIZE, Piece.PIECE_SIZE, true, true);
        backsidePieceIcons = new Image(getClass().getResourceAsStream("/icons/backside_piece_icons_outline.png"), 2 * Piece.PIECE_SIZE, Piece.PIECE_SIZE, true, true);
    }

    private void makeComponents() {
        boardArea = new BoardArea(tileBackground, pieceIcons, backsidePieceIcons);
        tray = new Tray(pieceIcons);

        northContainer = new TextFlow();
        northContainer.setTextAlignment(TextAlignment.CENTER);
        northContainer.setMinWidth(boardArea.getSize());
        northContainer.setPrefWidth(boardArea.getSize());
        //northContainer.setStyle("-fx-border-color: black");

        northPlayerName = new Text("Emperor of the North");
        northPlayerName.setFont(Font.font("Helvetica", FontWeight.BOLD, 40));
        northPlayerName.setFill(Color.web("#48a4f9"));
        northPlayerName.setStroke(Color.WHITE);
        northPlayerName.setStrokeWidth(1);

        northContainer.getChildren().add(northPlayerName);

        southContainer = new TextFlow();
        southContainer.setTextAlignment(TextAlignment.CENTER);
        southContainer.setMinWidth(boardArea.getSize());
        southContainer.setPrefWidth(boardArea.getSize());
        //southContainer.setStyle("-fx-border-color: black");

        southPlayerName = new Text("Emperor of the South");
        southPlayerName.setFont(Font.font("Helvetica", FontWeight.BOLD, 40));
        southPlayerName.setFill(Color.web("#bf1c1c"));
        southPlayerName.setStroke(Color.WHITE);
        southPlayerName.setStrokeWidth(1);

        southContainer.getChildren().add(southPlayerName);

        getChildren().addAll(boardArea, tray, northContainer, southContainer);
    }

    private void placeComponents(double padding) {
        boardArea.setLayoutX(90 + padding);
        boardArea.setLayoutY(10 + 45);

        tray.setLayoutX(0 + padding);
        tray.setLayoutY(0 + 45);

        northContainer.setLayoutX(90 + padding);
        northContainer.setLayoutY(0);

        southContainer.setLayoutX(90 + padding);
        southContainer.setLayoutY(10 + 45 + boardArea.getSize());
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

    public void processGameOver() {
        boardArea.revealAll();
    }

    public void processPiecePlaced(int playerIndex, int pieceIndex, int row, int col) {
        System.out.println("Piece placed at (" + row + "|" + col + "): " + PieceType.values()[pieceIndex] + " (InGameView).");
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

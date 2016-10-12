package project.stratego.ui.menus;

import javafx.geometry.Insets;
import project.stratego.control.CommunicationManager;
import project.stratego.ui.StrategoFrame;
import project.stratego.ui.components.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by Simon on 17/09/2016.
 */
public class InGameView extends Pane {

    private BoardArea boardArea;
    private Tray trayNorth, traySouth;

    private Image tileBackground;
    private Image pieceIcons;
    private Image backsidePieceIcons;

    public InGameView(double padding) {
        super();
        loadImages();
        makeComponents();
        placeComponents(padding);
        setPadding(new Insets(padding / 2));

        Rectangle test = new Rectangle(10, 10, 20, 20);
        test.setFill(Color.WHITE);
        test.setOnMouseClicked((MouseEvent e) -> {
            CommunicationManager.getInstance().sendAutoDeploy();
        });
        getChildren().add(test);
        setStyle("-fx-background-color: linear-gradient(from 0% 50% to 100% 50%, #48a4f9, #bf1c1c);");
    }

    private void loadImages() {
        tileBackground = new Image(getClass().getResourceAsStream("/icons/board_background_minimal.png"), 10 * BoardTile.TILE_SIZE, 10 * BoardTile.TILE_SIZE, true, true);
        pieceIcons = new Image(getClass().getResourceAsStream("/icons/piece_icons.png"), 12 * Piece.PIECE_SIZE, 2 * Piece.PIECE_SIZE, true, true);
        backsidePieceIcons = new Image(getClass().getResourceAsStream("/icons/backside_piece_icons.png"), 2 * Piece.PIECE_SIZE, Piece.PIECE_SIZE, true, true);
    }

    private void makeComponents() {
        boardArea = new BoardArea(tileBackground, pieceIcons, backsidePieceIcons);
        trayNorth = new Tray(0, pieceIcons);
        traySouth = new Tray(1, pieceIcons);
        getChildren().addAll(boardArea, trayNorth, traySouth);
    }

    private void placeComponents(double padding) {
        boardArea.setLayoutX(90 + padding);
        boardArea.setLayoutY(10 + padding);

        trayNorth.setLayoutX(745 + padding);
        trayNorth.setLayoutY(0 + padding);

        traySouth.setLayoutX(0 + padding);
        traySouth.setLayoutY(0 + padding);
    }

    public void processPiecePlaced(int playerIndex, int pieceIndex, int row, int col) {
        boardArea.makePiece(playerIndex, pieceIndex, row, col);
    }

    public void processPieceMoved(int orRow, int orCol, int destRow, int destCol) {
        boardArea.movePiece(orRow, orCol, destRow, destCol);
    }

    public void processResetDeployment(int playerIndex) {
        boardArea.resetDeployment(playerIndex);
    }

    public void processHidePiece(int row, int col) {
        boardArea.hidePiece(row, col);
    }

    public void processRevealPiece(int row, int col) {
        boardArea.revealPiece(row, col);
    }

    public void processAttackLost(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(stopRow, stopCol);
    }

    public void processAttackTied(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(stopRow, stopCol);
        boardArea.removePiece(destRow, destCol);
    }

    public void processAttackWon(int orRow, int orCol, int stopRow, int stopCol, int destRow, int destCol) {
        boardArea.movePiece(orRow, orCol, stopRow, stopCol);
        boardArea.attackAnimation(stopRow, stopCol, destRow, destCol);
        boardArea.removePiece(destRow, destCol);
        boardArea.movePiece(stopRow, stopCol, destRow, destCol);
    }

}

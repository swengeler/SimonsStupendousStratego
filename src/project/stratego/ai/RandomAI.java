package project.stratego.ai;

import project.stratego.control.managers.ModelComManager;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.*;
import project.stratego.game.logic.DeploymentLogic;
import project.stratego.game.utils.*;

import java.util.ArrayList;

public class RandomAI implements AIInterface {

    public MoveManager moveManager;

    public RandomAI() {
        moveManager = new MoveManager(null);
    }

    @Override
    public AIMove getNextMove(GameState state, int playerIndex) {
        moveManager.changeBoard(state.getBoardArray());
        ArrayList<Piece> movablePieces = getMovablePieces(state.getBoardArray(), state.getPlayer(playerIndex));;
        Piece selectedPiece = movablePieces.get((int) (Math.random() * movablePieces.size()));;
        int radius;
        int destRow, destCol;
        double r;

        System.out.println("Selected: " + selectedPiece.getType() + " at (" + selectedPiece.getRowPos() + "|" + selectedPiece.getColPos() + ").");

        do {
            radius = selectedPiece.getType() == PieceType.SCOUT ? (int) (Math.random() * 9 + 1) : 1;
            r = Math.random();
            destRow = r < 0.5 ? (r < 0.25 ? selectedPiece.getRowPos() + radius : selectedPiece.getRowPos() - radius) : selectedPiece.getRowPos();
            destCol = r >= 0.5 ? (r >= 0.75 ? selectedPiece.getColPos() + radius : selectedPiece.getColPos() - radius) : selectedPiece.getColPos();
            System.out.println("Test move to: (" + destRow + "|" + destCol + ").");
        } while (moveManager.testMove(selectedPiece, destRow, destCol) == MoveResult.NOMOVE);

        System.out.println(selectedPiece.getRowPos() + " " + selectedPiece.getColPos() + " " + destRow + " " + destCol);

        return new AIMove(selectedPiece.getRowPos(), selectedPiece.getColPos(), destRow, destCol);
    }

    @Override
    public void makeBoardSetup(GameState state, int playerIndex) {
        ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
    }

    private ArrayList<Piece> getMovablePieces(BoardTile[][] board, Player player) {
        ArrayList<Piece> movablePieces = new ArrayList<>();
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG &&
                    ((p.getRowPos() - 1 >= 0 && (board[p.getRowPos() - 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() - 1][p.getColPos()].isAccessible()) ||
                    (p.getRowPos() + 1 < 10 && (board[p.getRowPos() + 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() + 1][p.getColPos()].isAccessible()) ||
                    (p.getColPos() - 1 >= 0 && (board[p.getRowPos()][p.getColPos() - 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() - 1].isAccessible()) ||
                    (p.getColPos() + 1 < 10 && (board[p.getRowPos()][p.getColPos() + 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() + 1].isAccessible()))) {
                movablePieces.add(p);
            }
        }
        return movablePieces;
    }

}

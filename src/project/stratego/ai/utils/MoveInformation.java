package project.stratego.ai.utils;

import project.stratego.game.entities.Piece;
import project.stratego.game.utils.Move;
import project.stratego.game.utils.MoveResult;

import java.util.HashMap;

public class MoveInformation implements Move {

    private Piece movingPieceReference, movingPieceClone, encounteredPieceReference, encounteredPieceClone;

    private double[][] previousProbabilities;

    private MoveResult moveResult;

    private int orRow, orCol, destRow, destCol;

    public MoveInformation(Move move, Piece movingPiece, Piece encounteredPiece) {
        orRow = move.getOrRow();
        orCol = move.getOrCol();
        destRow = move.getDestRow();
        destCol = move.getDestCol();
        this.movingPieceReference = movingPiece;
        this.movingPieceClone = movingPiece.clone();
        if (encounteredPiece != null) {
            this.encounteredPieceReference = encounteredPiece;
            this.encounteredPieceClone = encounteredPiece.clone();
        }
    }

    public void setMoveResult(MoveResult moveResult) {
        this.moveResult = moveResult;
    }

    public void setPreviousProbabilities(HashMap<Piece, double[]> probabilitiesMap) {
        previousProbabilities = new double[40][0];
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            previousProbabilities[counter++] = probabilitiesMap.get(p).clone();
        }
    }

    public MoveResult getMoveResult() {
        return moveResult;
    }

    public Piece getMovingPieceReference() {
        return movingPieceReference;
    }

    public Piece getMovingPieceClone() {
        return movingPieceClone;
    }

    public Piece getEncounteredPieceClone() {
        return encounteredPieceClone;
    }

    public Piece getEncounteredPieceReference() {
        return encounteredPieceReference;
    }

    public void replaceProbabilities(HashMap<Piece, double[]> probabilitiesMap) {
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            probabilitiesMap.replace(p, previousProbabilities[counter++]);
        }
    }

    @Override
    public int getOrRow() {
        return orRow;
    }

    @Override
    public int getOrCol() {
        return orCol;
    }

    @Override
    public int getDestRow() {
        return destRow;
    }

    @Override
    public int getDestCol() {
        return destCol;
    }

    @Override
    public int length() {
        return Math.abs(destRow - orRow + destCol - orCol);
    }

    @Override
    public int getPlayerIndex() {
        return movingPieceReference.getPlayerType().ordinal();
    }

}

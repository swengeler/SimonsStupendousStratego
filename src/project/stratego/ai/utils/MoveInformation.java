package project.stratego.ai.utils;

import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.moves.MoveResult;

import java.util.HashMap;

public class MoveInformation {

    private Piece movingPieceReference, movingPieceClone, encounteredPieceReference, encounteredPieceClone;

    private double[][] previousProbabilities;

    private HashMap<Piece, double[]> previousProbabilitiesMap;

    private MoveResult moveResult;

    private int orRow, orCol, destRow, destCol, playerWonIndex;

    MoveInformation(Move move, Piece movingPiece, Piece encounteredPiece) {
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

    public void setPlayerWonIndex(int playerWonIndex) {
        this.playerWonIndex = playerWonIndex;
    }

    void setMoveResult(MoveResult moveResult) {
        this.moveResult = moveResult;
    }

    void setPreviousProbabilities(HashMap<Piece, double[]> probabilitiesMap) {
        /*previousProbabilities = new double[40][0];
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            previousProbabilities[counter++] = probabilitiesMap.get(p).clone();
        }*/
        previousProbabilitiesMap = new HashMap<>();
        for (Piece p : probabilitiesMap.keySet()) {
            previousProbabilitiesMap.put(p.clone(), probabilitiesMap.get(p).clone());
        }
    }

    int getPlayerWonIndex() {
        return playerWonIndex;
    }

    MoveResult getMoveResult() {
        return moveResult;
    }

    Piece getMovingPieceReference() {
        return movingPieceReference;
    }

    Piece getMovingPieceClone() {
        return movingPieceClone;
    }

    Piece getEncounteredPieceClone() {
        return encounteredPieceClone;
    }

    Piece getEncounteredPieceReference() {
        return encounteredPieceReference;
    }

    void replaceProbabilities(HashMap<Piece, double[]> probabilitiesMap) {
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            //probabilitiesMap.replace(p, previousProbabilities[counter++]);
            probabilitiesMap.put(p, previousProbabilitiesMap.get(p));
        }
    }

    public int getOrRow() {
        return orRow;
    }

    public int getOrCol() {
        return orCol;
    }

    public int getDestRow() {
        return destRow;
    }

    public int getDestCol() {
        return destCol;
    }

    public int getPlayerIndex() {
        return movingPieceReference.getPlayerType().ordinal();
    }

}

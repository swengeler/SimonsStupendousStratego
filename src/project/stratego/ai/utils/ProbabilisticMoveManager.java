package project.stratego.ai.utils;

import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.MoveManager;
import project.stratego.game.utils.PieceType;

import java.util.HashMap;

public class ProbabilisticMoveManager extends MoveManager {

    private HashMap<Piece, double[]> probabilitiesMap;

    public ProbabilisticMoveManager(BoardTile[][] board, HashMap<Piece, double[]> probabilitiesMap) {
        super(board);
        this.probabilitiesMap = probabilitiesMap;
    }

    protected boolean checkIfAttackWins(Piece attackingPiece, Piece defendingPiece) {
        boolean opponentAttacking = probabilitiesMap.containsKey(attackingPiece);
        Piece opponentPiece = opponentAttacking ? attackingPiece : defendingPiece;

        // find the rank of the unrevealed opponent piece
        double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
        PieceType opponentPieceType = PieceType.FLAG;
        boolean found = false;
        for (int i = 0; i < probabilitiesArray.length && !found; i++) {
            if (Math.abs(probabilitiesArray[i] - 1) < EnhancedGameState.PROB_EPSILON) {
                opponentPieceType = PieceType.values()[i];
                found = true;
            }
        }

        //System.out.println("Opponent " + opponentPiece + " has type " + opponentPieceType + " in conflict with " + (opponentAttacking ? defendingPiece : attackingPiece));

        PieceType attackingPieceType;
        PieceType defendingPieceType;

        if (opponentAttacking) {
            attackingPieceType = opponentPieceType;
            defendingPieceType = defendingPiece.getType();
        } else {
            attackingPieceType = attackingPiece.getType();
            defendingPieceType = opponentPieceType;
        }

        if (attackingPieceType == PieceType.SPY && defendingPieceType == PieceType.MARSHAL || attackingPieceType == PieceType.MARSHAL && defendingPieceType == PieceType.SPY) {
            return true;
        }
        if (attackingPieceType == PieceType.MINER && defendingPieceType == PieceType.BOMB) {
            return true;
        }
        return (PieceType.pieceLvlMap.get(attackingPieceType)) > (PieceType.pieceLvlMap.get(defendingPieceType));
    }

    @Override
    protected boolean checkIfDraw(Piece attackingPiece, Piece defendingPiece) {
        boolean opponentAttacking = probabilitiesMap.containsKey(attackingPiece);
        Piece opponentPiece = opponentAttacking ? attackingPiece : defendingPiece;
        Piece ownPiece = opponentAttacking ? defendingPiece : attackingPiece;

        // find the rank of the unrevealed opponent piece
        double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
        PieceType opponentPieceType = PieceType.FLAG;
        boolean found = false;
        for (int i = 0; i < probabilitiesArray.length && !found; i++) {
            if (Math.abs(probabilitiesArray[i] - 1) < EnhancedGameState.PROB_EPSILON) {
                opponentPieceType = PieceType.values()[i];
                found = true;
            }
        }

        return opponentPieceType == ownPiece.getType();
    }

}

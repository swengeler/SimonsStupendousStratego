package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class TestEvaluationFunction extends AbstractEvaluationFunction {

    private double allPiecesSum = 0.0;

    public TestEvaluationFunction(int playerIndex) {
        super(playerIndex);
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if (PieceType.values()[i] == PieceType.BOMB) {
                allPiecesSum += PieceType.pieceQuantity[i] * 4.5;
            } else if (PieceType.values()[i] == PieceType.FLAG) {
                allPiecesSum += 100.0;
            } else {
                allPiecesSum += PieceType.pieceQuantity[i] * PieceType.pieceLvlMap.get(PieceType.values()[i]);
            }
        }
        System.out.println("Piece sum: " + allPiecesSum);
    }

    @Override
    public double evaluate(EnhancedGameState gameState, int playerType) {
        /*if (gameState.isGameOver() && gameState.playerWon()) {
            return 3.0;
        } else if (gameState.isGameOver()) {
            return 0.0;
        }*/

        /*double opponentSum = 0;
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                opponentSum += 4.5;
            } else if (p.getType() == PieceType.FLAG) {
                opponentSum += 100.0;
            } else {
                opponentSum += PieceType.pieceLvlMap.get(p.getType());
            }
        }
        double ownSum = 0;
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                ownSum += 4.5;
            } else if (p.getType() == PieceType.FLAG) {
                ownSum += 100.0;
            } else {
                ownSum += PieceType.pieceLvlMap.get(p.getType());
            }
        }*/
        boolean found = false;
        double opponentSum = allPiecesSum;
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getDeadPieces()) {
            if (Math.abs(gameState.getProbability(p, p.getType()) - 1.0) < EnhancedGameState.PROB_EPSILON) {

            } else {
                for (int i = 0; !found && i < PieceType.values().length - 1; i++) {
                    if (Math.abs(gameState.getProbability(p, i) - 1.0) < EnhancedGameState.PROB_EPSILON) {
                        found = true;
                        if (i == 1) {
                            opponentSum -= 4.5;
                        } else if (i == 0) {
                            opponentSum -= 100.0;
                        } else {
                            opponentSum -= PieceType.pieceLvlMap.get(PieceType.values()[i]);
                        }
                    }
                }
            }
            found = false;
        }
        double ownSum = 0.0;
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                ownSum += 4.5;
            } else if (p.getType() == PieceType.FLAG) {
                ownSum += 100.0;
            } else {
                ownSum += PieceType.pieceLvlMap.get(p.getType());
            }
        }

        if (opponentSum != 0) {
            double randomFactor = Math.random() * 0.05;
            return (ownSum / opponentSum) /*+ randomFactor*/;
        }
        return 1000.0;
    }

}

package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class TestEvaluationFunction extends AbstractEvaluationFunction {

    public TestEvaluationFunction(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public double evaluate(EnhancedGameState gameState, int playerType) {
        if (gameState.isGameOver() && gameState.playerWon()) {
            return 1000.0;
        } else if (gameState.isGameOver()) {
            return 0.0;
        }

        double opponentSum = 0;
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                opponentSum += 4.5;
            } else if (p.getType() == PieceType.FLAG) {
                opponentSum += 20.0;
            } else {
                opponentSum += PieceType.pieceLvlMap.get(p.getType());
            }
        }
        double ownSum = 0;
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                ownSum += 4.5;
            } else if (p.getType() == PieceType.FLAG) {
                ownSum += 20.0;
            } else {
                ownSum += PieceType.pieceLvlMap.get(p.getType());
            }
        }
        if (opponentSum != 0) {
            return ownSum / opponentSum;
        }
        return 1000.0;
    }

}

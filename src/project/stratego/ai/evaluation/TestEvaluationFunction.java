package project.stratego.ai.evaluation;

import project.stratego.ai.EnhancedGameState;
import project.stratego.ai.searchGenerics.GenericEvaluationFunction;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

public class TestEvaluationFunction extends GenericEvaluationFunction {

    public TestEvaluationFunction(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public double evaluate(EnhancedGameState gameState) {
        /*if (gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces().size() != 0) {
            return gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces().size() / gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces().size();
        }*/
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

package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

public abstract class AbstractEvaluationFunction {

    protected int playerIndex;

    protected AbstractEvaluationFunction(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public abstract double evaluate(EnhancedGameState state, int playerType);

    public boolean won(EnhancedGameState state) {
        for (Piece p : state.getPlayer(1 - state.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.FLAG) {
                return true;
            }
        }
        return true;
    }

}

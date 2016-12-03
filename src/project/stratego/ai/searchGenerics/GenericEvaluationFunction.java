package project.stratego.ai.searchGenerics;

import project.stratego.ai.EnhancedGameState;

public abstract class GenericEvaluationFunction {

    protected int playerIndex;

    protected GenericEvaluationFunction(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public abstract double evaluate(EnhancedGameState state);

}

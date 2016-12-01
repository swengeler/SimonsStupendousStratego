package project.stratego.ai.searchGenerics;

import project.stratego.ai.EnhancedGameState;

public interface EvaluationFunction {

    double evaluate(EnhancedGameState gameState);

}

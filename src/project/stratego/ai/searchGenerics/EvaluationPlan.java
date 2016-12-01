package project.stratego.ai.searchGenerics;

import project.stratego.ai.EnhancedGameState;

public interface EvaluationPlan {

    double evaluate(EnhancedGameState gameState);

}

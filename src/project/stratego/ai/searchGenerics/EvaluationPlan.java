package project.stratego.ai.searchGenerics;

import project.stratego.ai.utils.EnhancedGameState;

public interface EvaluationPlan {

    double evaluate(EnhancedGameState gameState);

}

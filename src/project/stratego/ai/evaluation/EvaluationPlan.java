package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;

public interface EvaluationPlan {

    double evaluate(EnhancedGameState gameState);

}

package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;

import java.util.ArrayList;

public class InvincibleEvaluationFunction extends GenericEvaluationFunction {

    private ArrayList<EvaluationPlan> evaluationPlans;

    public InvincibleEvaluationFunction(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public double evaluate(EnhancedGameState gameState) {
        double sum = 0;
        for (EvaluationPlan p : evaluationPlans) {
            sum += p.evaluate(gameState);
        }
        return sum;
    }

}

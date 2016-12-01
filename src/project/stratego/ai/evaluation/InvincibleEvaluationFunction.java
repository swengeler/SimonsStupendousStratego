package project.stratego.ai.evaluation;

import project.stratego.ai.EnhancedGameState;
import project.stratego.ai.searchGenerics.EvaluationFunction;
import project.stratego.ai.searchGenerics.EvaluationPlan;

import java.util.ArrayList;

public class InvincibleEvaluationFunction implements EvaluationFunction {

    private ArrayList<EvaluationPlan> evaluationPlans;

    @Override
    public double evaluate(EnhancedGameState gameState) {
        double sum = 0;
        for (EvaluationPlan p : evaluationPlans) {
            sum += p.evaluate(gameState);
        }
        return sum;
    }

}

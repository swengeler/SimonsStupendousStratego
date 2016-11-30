package project.stratego.ai.evaluation;

import project.stratego.ai.searchGenerics.EvaluationFunction;
import project.stratego.ai.searchGenerics.EvaluationPlan;
import project.stratego.game.entities.GameState;

import java.util.ArrayList;

public class InvincibleEvaluationFunction implements EvaluationFunction {

    private ArrayList<EvaluationPlan> evaluationPlans;

    @Override
    public double evaluate(GameState state, int playerIndex) {
        double sum = 0;
        for (EvaluationPlan p : evaluationPlans) {
            sum += p.evaluate(state, playerIndex);
        }
        return sum;
    }

}

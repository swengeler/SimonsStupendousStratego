package project.stratego.ai.searchGenerics;

import project.stratego.game.entities.GameState;

import java.util.ArrayList;

public class EvaluationFunction {

    private ArrayList<EvaluationPlan> evaluationPlens;

    public double evaluate(GameState state, int playerIndex) {
        double sum = 0;
        for (EvaluationPlan p : evaluationPlens) {
            sum += p.evaluate(state, playerIndex);
        }
        return sum;
    }

}

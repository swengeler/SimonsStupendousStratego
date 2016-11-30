package project.stratego.ai.evaluation;

import project.stratego.ai.searchGenerics.EvaluationFunction;
import project.stratego.game.entities.GameState;

public class TestEvaluationFunction implements EvaluationFunction {

    @Override
    public double evaluate(GameState state, int playerIndex) {
        return 0;
    }

}

package project.stratego.ai.searchGenerics;

import project.stratego.game.entities.GameState;

public interface EvaluationFunction {

    double evaluate(GameState state, int playerIndex);

}

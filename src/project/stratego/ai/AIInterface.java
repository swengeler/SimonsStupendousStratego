package project.stratego.ai;

import project.stratego.game.entities.*;
import project.stratego.game.utils.Move;

public interface AIInterface {

    Move getNextMove(GameState state, int playerIndex);
    void makeBoardSetup(GameState state, int playerIndex);

}

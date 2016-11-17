package project.stratego.ai;

import project.stratego.game.entities.*;

public interface AIInterface {

    AIMove getNextMove(GameState state, int playerIndex);
    void makeBoardSetup(GameState state, int playerIndex);

}

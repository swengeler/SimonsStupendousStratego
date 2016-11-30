package project.stratego.ai.searchGenerics;

import project.stratego.game.entities.*;
import project.stratego.game.utils.Move;

public interface AIInterface {

    Move getNextMove(Move lastOpponentMove);
    int getPlayerIndex();
    void makeBoardSetup(GameState state);
    void copyOpponentSetup(GameState state);

}

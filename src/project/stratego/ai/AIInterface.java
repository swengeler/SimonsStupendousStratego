package project.stratego.ai;

import project.stratego.game.StrategoGame;
import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Player;

public interface AIInterface {

    AIMove getNextMove(BoardTile[][] board, Player player);
    void makeBoardSetup(StrategoGame game, int playerIndex);

}

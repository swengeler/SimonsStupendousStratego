package project.stratego.ai.utils;

import project.stratego.game.moves.Move;
import project.stratego.game.utils.PlayerType;

public class AIMove extends Move {

    private boolean isChanceMove;

    public AIMove(int playerIndex, int orRow, int orCol, int destRow, int destCol, boolean isChanceMove) {
        super(playerIndex, orRow, orCol, destRow, destCol);
        this.isChanceMove = isChanceMove;
    }

    public boolean isChanceMove() {
        return isChanceMove;
    }

    @Override
    public String toString() {
        return "AIMove (isChanceMove = " + isChanceMove + ") FROM (" + orRow + "|" + orCol + ") TO (" + destRow + "|" + destCol + ") by player " + PlayerType.values()[playerIndex];
    }

    @Override
    public AIMove clone() {
        return new AIMove(playerIndex, orRow, orCol, destRow, destCol, isChanceMove);
    }

}

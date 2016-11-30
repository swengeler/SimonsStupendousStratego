package project.stratego.ai.searchGenerics;

import project.stratego.game.utils.Move;

public class AIMove implements Move {

    private final int orRow, orCol, destRow, destCol;

    private final boolean isChanceMove;

    public AIMove(int orRow, int orCol, int destRow, int destCol, boolean isChanceMove) {
        this.orRow = orRow;
        this.orCol = orCol;
        this.destRow = destRow;
        this.destCol = destCol;
        this.isChanceMove = isChanceMove;
    }

    @Override
    public int getOrRow() {
        return orRow;
    }

    @Override
    public int getOrCol() {
        return orCol;
    }

    @Override
    public int getDestRow() {
        return destRow;
    }

    @Override
    public int getDestCol() {
        return destCol;
    }

    @Override
    public int length() {
        return Math.abs(destRow - orRow + destCol - orCol);
    }

    public boolean isChanceMove() {
        return isChanceMove;
    }

}

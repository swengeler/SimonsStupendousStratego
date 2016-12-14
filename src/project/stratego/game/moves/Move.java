package project.stratego.game.moves;

import project.stratego.game.utils.PlayerType;

public class Move {

    protected int playerIndex, orRow, orCol, destRow, destCol;

    public Move(int playerIndex, int orRow, int orCol, int destRow, int destCol) {
        this.playerIndex = playerIndex;
        this.orRow = orRow;
        this.orCol = orCol;
        this.destRow = destRow;
        this.destCol = destCol;
    }

    public Move(Move move) {
        playerIndex = move.getPlayerIndex();
        orRow = move.getOrRow();
        orCol = move.getOrCol();
        destRow = move.getDestRow();
        destCol = move.getDestCol();
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public int getOrRow() {
        return orRow;
    }

    public int getOrCol() {
        return orCol;
    }

    public int getDestRow() {
        return destRow;
    }

    public int getDestCol() {
        return destCol;
    }

    public int length() {
        return Math.abs(destRow - orRow + destCol - orCol);
    }

    @Override
    public String toString() {
        return "Move FROM (" + orRow + "|" + orCol + ") TO (" + destRow + "|" + destCol + ") by player " + PlayerType.values()[playerIndex];
    }

    @Override
    public Move clone() {
        return new Move(playerIndex, orRow, orCol, destRow, destCol);
    }


}

package project.stratego.game.moves;

public class Move {

    private int playerIndex, orRow, orCol, destRow, destCol;

    public Move(int playerIndex, int orRow, int orCol, int destRow, int destCol) {
        this.playerIndex = playerIndex;
        this.orRow = orRow;
        this.orCol = orCol;
        this.destRow = destRow;
        this.destCol = destCol;
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
    public Move clone() {
        return new Move(playerIndex, orRow, orCol, destRow, destCol);
    }


}

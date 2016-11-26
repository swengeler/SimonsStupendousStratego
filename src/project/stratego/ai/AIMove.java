package project.stratego.ai;

public class AIMove {

    public final int orRow, orCol, destRow, destCol;

    public final boolean isChanceMove;

    public AIMove(int orRow, int orCol, int destRow, int destCol, boolean isChanceMove) {
        this.orRow = orRow;
        this.orCol = orCol;
        this.destRow = destRow;
        this.destCol = destCol;
        this.isChanceMove = isChanceMove;
    }

}

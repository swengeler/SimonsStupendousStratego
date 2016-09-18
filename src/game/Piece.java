package game;

/**
 * Created by Simon on 17/09/2016.
 */
public class Piece {

    private static int pieceCount;
    private int ID;

    private PieceType type;

    private int xPos;
    private int yPos;

    public Piece(PieceType type, int xPos, int yPos) {
        ID = pieceCount++;
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getID() {
        return ID;
    }

    public PieceType getType() {
        return type;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }
}

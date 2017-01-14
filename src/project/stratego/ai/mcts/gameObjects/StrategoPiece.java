package project.stratego.ai.mcts.gameObjects;

public class StrategoPiece {
  
	private int pieceID;
	private static int pieceCount = 0 ;
	private PieceType pieceType;

	private int xPos = -100;
	private int yPos = -100;

	public StrategoPiece(PieceType pieceType) {
		pieceID = pieceCount;
		pieceCount++;
		this.pieceType = pieceType;
	}

	public int getPieceID() {
		return pieceID;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public StrategoPiece copyPiece() {
		StrategoPiece copy = new StrategoPiece(pieceType);
		copy.setxPos(xPos);
		copy.setyPos(yPos);
		return copy;
	}

	@Override
	public String toString() {
		return "StrategoPiece [pieceID=" + pieceID + ", pieceType=" + pieceType + ", xPos=" + xPos + ", yPos=" + yPos
				+ "]";
	}

}

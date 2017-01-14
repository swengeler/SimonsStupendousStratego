package project.stratego.ai.mcts.events;

import project.stratego.ai.mcts.gameObjects.PieceType;

public class DeploymentEvent extends StrategoAbstractEvent {

	private PieceType pieceType;

	private int xCoord;

	private int yCoord;

	public DeploymentEvent(PieceType pieceType, int xCoord, int yCoord) {
		super();
		this.pieceType = pieceType;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public int getxCoord() {
		return xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

}

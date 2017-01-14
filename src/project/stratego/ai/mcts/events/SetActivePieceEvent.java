package project.stratego.ai.mcts.events;

public class SetActivePieceEvent extends StrategoAbstractEvent {

	private int xCoord;
	private int yCoord;

	public SetActivePieceEvent(int x, int y) {

		this.xCoord = x;
		this.yCoord = y;
	}

	public int getxCoord() {
		return xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

}

package project.stratego.ai.mcts.events;


public class StrategoMoveEvent extends StrategoAbstractEvent {

	private int pathLength = 1;

	private int destX;
	private int destY;
	private int originX;
	private int originY;

	public StrategoMoveEvent(int dX, int dY, int dL) {
		this.pathLength = dL;
		this.destX = dX;
		this.destY = dY;
	}

	public StrategoMoveEvent(int originX, int originY, int destX, int destY) {

		this.destX = destX;
		this.destY = destY;
		this.originX = originX;
		this.originY = originY;

	}
	public int getDestX() {
		return destX;
	}

	public int getDestY() {
		return destY;
	}

	public int getPathLength() {
		return pathLength;
	}

	public void setPathLength(int pathLength) {
		this.pathLength = pathLength;
	}

	public int getOrigintX() {
		return originX;
	}

	public void setOriginX(int targetX) {
		this.originX = targetX;
	}

	public int getOriginY() {
		return originY;
	}

	public void setOriginY(int targetY) {
		this.originY = targetY;
	}

	@Override
	public String toString() {
		return "StrategoMoveEvent [destX=" + destX + ", destY=" + destY + ", originX=" + originX + ", originY=" + originY + "]";
	}

}

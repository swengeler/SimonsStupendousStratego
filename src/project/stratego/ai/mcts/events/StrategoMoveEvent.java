package project.stratego.ai.mcts.events;


public class StrategoMoveEvent extends StrategoAbstractEvent {



	private int pathLength = 1;

	private int dX;

	private int dY;
	
	private int originX;

	private int originY;

	public StrategoMoveEvent(int dX, int dY, int dL) {
		this.pathLength = dL;
		this.dX = dX;
		this.dY = dY;
	}

	public StrategoMoveEvent(int originX, int originY, int destX, int destY) {

		this.dX = destX;
		this.dY = destY;
		this.originX = originX;
		this.originY = originY;

	}
	public int getdX() {
		return dX;
	}

	public int getdY() {
		return dY;
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
    return "StrategoMoveEvent [dX=" + dX + ", dY=" + dY + ", originX=" + originX + ", originY=" + originY + "]";
  }

}

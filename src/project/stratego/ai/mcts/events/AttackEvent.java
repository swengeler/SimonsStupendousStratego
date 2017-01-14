package project.stratego.ai.mcts.events;

public class AttackEvent extends StrategoMoveEvent {

	public AttackEvent(int dX, int dY, int dL) {
		super(dX, dY, dL);

	}

	public int getDestX() {
		return super.getDestX();
	}

	public int getDestY() {
		return super.getDestY();
	}

}

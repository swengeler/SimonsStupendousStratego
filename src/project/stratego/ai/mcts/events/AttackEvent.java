package project.stratego.ai.mcts.events;

public class AttackEvent extends StrategoMoveEvent {

	public AttackEvent(int dX, int dY, int dL) {
		super(dX, dY, dL);

	}

	public int getdX() {
		return super.getdX();
	}

	public int getdY() {
		return super.getdY();
	}

}

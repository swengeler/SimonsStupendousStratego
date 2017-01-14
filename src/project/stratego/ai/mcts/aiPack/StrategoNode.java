package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;

public class StrategoNode extends TreeNode<StrategoGame, StrategoAbstractEvent> {

	private boolean isAttackNode;

	private StrategoNode[] outcomeNodeList = new StrategoNode[3];

	public StrategoNode(StrategoGame rootState) {
		super(rootState);

	}

	public StrategoNode[] getOutcomeNodeList() {
		return outcomeNodeList;
	}

	public void setOutcomeNodeList(StrategoNode[] outcomeNodeList) {
		this.outcomeNodeList = outcomeNodeList;
	}

	public boolean isAttackNode() {
		return isAttackNode;
	}

	public void setAttackNode(boolean isAttackNode) {
		this.isAttackNode = isAttackNode;
	}

}

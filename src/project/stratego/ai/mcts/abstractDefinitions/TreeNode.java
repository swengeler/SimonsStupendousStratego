package project.stratego.ai.mcts.abstractDefinitions;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<State extends SearchState, Action extends AbstractAction<State>> {

	private State state;

	private Action action;

	private ArrayList<TreeNode<State, Action>> childrenList;

	private int nodeDepth;

	/** Value of the Node */
	// private double valueOfNode;

	private double gamesPlayed = 0;
	private double gamesWon = 0;

	private TreeNode<State, Action> parentNode;

	private boolean isPlaythoughNode;

	public TreeNode(State rootState) {
		childrenList = new ArrayList<TreeNode<State, Action>>();
		this.setState(rootState);
	}

	public ArrayList<TreeNode<State, Action>> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<TreeNode<State, Action>> childrenList) {
		this.childrenList = (ArrayList<TreeNode<State, Action>>) childrenList;
	}

	public double getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(double gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public double getGamesWon() {
		return gamesWon;
	}

	public void setGamesWon(double gamesWon) {
		this.gamesWon = gamesWon;
	}

	public int getNodeDepth() {
		return nodeDepth;
	}

	public void setNodeDepth(int nodeDepth) {
		this.nodeDepth = nodeDepth;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public AbstractAction<State> getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public TreeNode<State, Action> getParent() {

		return parentNode;
	}

	public void setParent(TreeNode<State, Action> aNode) {
		this.parentNode = aNode;

	}

	public TreeNode<State, Action> getRootNode() {
		TreeNode<State, Action> tempNode = this;
		while (tempNode.getParent() != null)
			tempNode = tempNode.getParent();

		return tempNode;
	}

	// TODO Debug, remove
	public boolean isPlaythoughNode() {
		return isPlaythoughNode;
	}

	public void setPlaythoughNode(boolean isPlaythoughNode) {
		this.isPlaythoughNode = isPlaythoughNode;
	}

}

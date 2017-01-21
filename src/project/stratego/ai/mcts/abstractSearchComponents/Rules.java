package project.stratego.ai.mcts.abstractSearchComponents;

import project.stratego.ai.mcts.abstractDefinitions.SearchState;
import project.stratego.ai.mcts.abstractDefinitions.TreeNode;

public  abstract class Rules<State extends SearchState> {
	
	public abstract boolean isTerminal(TreeNode<State, ?> aNode);

	public abstract boolean isTerminal(State aState);

	public abstract int getScoreValue(TreeNode<State, ?> aNode, int referance);

	public abstract double getScoreValue(State state, int referance);

}



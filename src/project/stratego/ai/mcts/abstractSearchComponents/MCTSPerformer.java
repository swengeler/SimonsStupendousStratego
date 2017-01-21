package project.stratego.ai.mcts.abstractSearchComponents;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.AbstractAction;
import project.stratego.ai.mcts.abstractDefinitions.SearchState;
import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.logger.Logger;

public class MCTSPerformer<State extends SearchState, Action extends AbstractAction<State>> {

	protected Playthrough<State, Action> playthrough;
	protected Selection<State, Action> selection;

	protected Rules<State> rules;
	protected MoveGenerator<State, Action> moveGenerator;
	protected final int noOfItterations = 5000;

	public MCTSPerformer(Rules<State> rules, MoveGenerator<State, Action> moveGenerator) {
		this.rules = rules;
		this.moveGenerator = moveGenerator;
		this.playthrough = new Playthrough<State, Action>(moveGenerator, rules);
		this.selection = new Selection<State, Action>();

	}

	public MCTSPerformer(Rules<State> rules, MoveGenerator<State, Action> moveGenerator, Playthrough<State, Action> playthrough) {
		this.rules = rules;
		this.moveGenerator = moveGenerator;
		this.playthrough = playthrough;
		this.selection = new Selection<State, Action>();

	}


	public TreeNode<State, Action> runMCTS(TreeNode<State, Action> rootNode) {

		for (int i = 0; i < noOfItterations; i++) {

			mctsItteration(rootNode);

			// Logger.println("number of  Tottal itterations  : " + (i + 1));
		}
		// Logger.println("" + rootNode.getGamesPlayed());

		Logger.println(rootNode.getState().toString());

		return getBestChild(rootNode);

	}

	public void mctsItteration(TreeNode<State, Action> rootNode) {
		// Logger.println("start mcts itterattion  (Root  times played): " + rootNode.getGamesPlayed());
		TreeNode<State, Action> visititedNode = rootNode;

		while (visititedNode != null && !checkIfLeafNode(visititedNode) && !rules.isTerminal(visititedNode.getState())) {
			TreeNode<State, Action> selectChild = selection.selectChild(visititedNode);
			if (selectChild == null) {
				break;
			} else {
				visititedNode = selectChild;
			}
		}

		// Logger.println("Leaf node #of available moves  : " + moves.size());
		if (rules.isTerminal(visititedNode)) {
			// System.out.println("terminal node");

			double result = playthrough.returnPlaythroughResult(visititedNode);

			updateTree(visititedNode, result);

			return;
		}
		ArrayList<Action> moves = moveGenerator.generateAvailiableMoves(visititedNode.getState());

		addChildNodes(visititedNode, moves);
		visititedNode = selection.selectChild(visititedNode);
		double result = playthrough.returnPlaythroughResult(visititedNode);
		// Logger.println("playthrough result: " + result);
		updateTree(visititedNode, result);
		// Logger.println("end mcts itterattion  (Root  times played): " + rootNode.getGamesPlayed());

	}

	protected boolean checkIfLeafNode(TreeNode<State, Action> aNode) {
    // return aNode.getChildrenList().size() == 0;
    return aNode.getGamesPlayed() == 0;
	}

	protected void updateTree(TreeNode<State, Action> visitNode, double result) {
		TreeNode<State, Action> tempNode = visitNode;
		singleNodeUpdate(visitNode, result);
		while (tempNode.getParent() != null) {
			tempNode = tempNode.getParent();
			singleNodeUpdate(tempNode, result);

		}
	}

	protected void singleNodeUpdate(TreeNode<State, Action> visitNode, double leafPlaythroughResult) {

		if (visitNode.getNodeDepth() % 2 == 0) {
			leafPlaythroughResult *= -1;
		}
		double effectiveResult = leafPlaythroughResult + 1;
		visitNode.setGamesPlayed(visitNode.getGamesPlayed() + 2);
		

		visitNode.setGamesWon(visitNode.getGamesWon() + effectiveResult);

		
	}

	protected TreeNode<State, Action> getBestChild(TreeNode<State, Action> aNode) {

		Logger.println(aNode.getState().toString());
		ArrayList<TreeNode<State, Action>> childNodes = aNode.getChildrenList();
		TreeNode<State, Action> tempBestChild = childNodes.get(0);

		double tempCounter = Double.NEGATIVE_INFINITY;
		// if (tempBestChild.getGamesPlayed() != 0) {
		// tempCounter = childNodes.get(0).getGamesWon() / childNodes.get(0).getGamesPlayed();
		// } else {
		// tempCounter = 0;
		// }

		for (int i = 0; i < childNodes.size(); i++) {

			double compare = 0;

			if (childNodes.get(i).getGamesPlayed() != 0) {
				compare = 1.0*childNodes.get(i).getGamesWon() / childNodes.get(i).getGamesPlayed();
			} else {
				compare = 0;
			}
			Logger.println(childNodes.get(i).getAction() + " won " + childNodes.get(i).getGamesWon() + " played "
					+ childNodes.get(i).getGamesPlayed());

			if (compare > tempCounter) {
				tempCounter = compare;
				tempBestChild = childNodes.get(i);
			}
		}
		
		
		// Logger.println(""+tempBestChild.getGamesWon());
		return tempBestChild;
	}

	public void addChildNodes(TreeNode<State, Action> aNode, ArrayList<Action> moves) {

		for (int i = 0; i < moves.size(); i++) {
			@SuppressWarnings("unchecked")
			State newState = (State) aNode.getState().deepCopySelf();
			moveGenerator.applyAction(newState, moves.get(i));
			TreeNode<State, Action> newNode = new TreeNode<State, Action>(newState);
			newNode.setParent(aNode);
			aNode.getChildrenList().add(newNode);
			newNode.setNodeDepth(aNode.getNodeDepth() + 1);
			newNode.setAction(moves.get(i));
			// newNode.setPlaythoughNode(aNode.isPlaythoughNode());

	}
	}


}

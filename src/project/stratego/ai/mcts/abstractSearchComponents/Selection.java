package project.stratego.ai.mcts.abstractSearchComponents;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.AbstractAction;
import project.stratego.ai.mcts.abstractDefinitions.SearchState;
import project.stratego.ai.mcts.abstractDefinitions.TreeNode;

public class Selection<State extends SearchState, Action extends AbstractAction<State>> {
	
	
	public TreeNode<State, Action> selectChild(TreeNode<State, Action> aNode) {
		
		ArrayList<TreeNode<State, Action>> childrenList = aNode.getChildrenList();

		if (hasUnexploredChild(childrenList)) {
			for (int k = 0; k < childrenList.size(); k++) {
				if (childrenList.get(k).getGamesPlayed() == 0) {
					return childrenList.get(k);
				}

			}

		}

		// uct attempt
		TreeNode<State, Action> tempNode = null;
		double selectionReferance = 0;
		double tottalWins = 1;
		// System.out.println(selectionReferance);
		// System.out.println(tottalWins);
		for (int k = 0; k < childrenList.size(); k++) {
			// System.out.println("childs games won : " + childrenList.get(k).getGamesWon());
			tottalWins = tottalWins + childrenList.get(k).getGamesWon();
		}


		for (int i = 0; i < childrenList.size(); i++) {
			TreeNode<State, Action> examinedNode = childrenList.get(i);

			double selectionValue = 1.0 * examinedNode.getGamesWon() / examinedNode.getGamesPlayed() + Math.sqrt(2)
					* (Math.sqrt(Math.log(aNode.getGamesPlayed()) / examinedNode.getGamesPlayed()));

				if (selectionValue == Math.max(selectionValue, selectionReferance)) {
					selectionReferance = selectionValue;
					tempNode = examinedNode;

				}

		}

		return tempNode;
	}


	private boolean hasUnexploredChild(ArrayList<TreeNode<State, Action>> childrenList) {
		for (int k = 0; k < childrenList.size(); k++) {
			if (childrenList.get(k).getGamesPlayed() == 0) {
				return true;
			}

		}

		return false;
	}


	


}

package project.stratego.ai.mcts.aiPack;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.Selection;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;

public class StrategoMCTSSelection extends Selection<StrategoGame, StrategoAbstractEvent> {

	public TreeNode<StrategoGame, StrategoAbstractEvent> selectChild(TreeNode<StrategoGame, StrategoAbstractEvent> aNode) {

		ArrayList<TreeNode<StrategoGame, StrategoAbstractEvent>> childrenList = aNode.getChildrenList();

		if (hasUnexploredChild(childrenList)) {
			for (int k = 0; k < childrenList.size(); k++) {
				if (childrenList.get(k).getGamesPlayed() == 0) {
					return childrenList.get(k);
				}

			}

		}

		// uct attempt
		TreeNode<StrategoGame, StrategoAbstractEvent> tempNode = null;
		double selectionReferance = 0;
		double tottalWins = 1;

		for (int k = 0; k < childrenList.size(); k++) {

			tottalWins = tottalWins + childrenList.get(k).getGamesWon();
			if (tottalWins > 1) {
				tottalWins = tottalWins - 1;
			}
		}

		for (int i = 0; i < childrenList.size(); i++) {
			TreeNode<StrategoGame, StrategoAbstractEvent> examinedNode = childrenList.get(i);

			double selectionValue = 1.0 * examinedNode.getGamesWon() / examinedNode.getGamesPlayed() + 1.1
					* (Math.sqrt(Math.log(tottalWins) / examinedNode.getGamesPlayed()));

			// double selectionValue = 1.0 * examinedNode.getGamesWon() / examinedNode.getGamesPlayed() + Math.sqrt(2)
			// * (Math.sqrt(Math.log(tottalWins) / examinedNode.getGamesPlayed()));

			if (selectionValue == Math.max(selectionValue, selectionReferance)) {
				selectionReferance = selectionValue;
				tempNode = examinedNode;

			}

		}

		return tempNode;
	}

	private boolean hasUnexploredChild(ArrayList<TreeNode<StrategoGame, StrategoAbstractEvent>> childrenList) {
		for (int k = 0; k < childrenList.size(); k++) {
			if (childrenList.get(k).getGamesPlayed() == 0) {
				return true;
			}

		}

		return false;
	}

}

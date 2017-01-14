package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.*;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;

import java.util.ArrayList;

public class StrategoMctsPerformer extends MCTSperformer<StrategoGame, StrategoAbstractEvent> {

	private StrategoPlaythrough playthrough;

	public StrategoMctsPerformer(Rules<StrategoGame> rules,
								 MoveGenerator<StrategoGame, StrategoAbstractEvent> moveGenerator, StrategoPlaythrough playthrough) {
		super(rules, moveGenerator, playthrough);
		this.playthrough = playthrough;
	}

	@Override
	public void mctsItteration(TreeNode<StrategoGame, StrategoAbstractEvent> rootNode) {
		StrategoGame initGame = (StrategoGame) rootNode.getState().deepCopySelf();

    double scoreMultiplier = (initGame.getActivePlayer() == 1) ? 1.0 : -1.0;

		TreeNode<StrategoGame, StrategoAbstractEvent> visititedNode = rootNode;
		// int rootPieces = rootNode.getState().getPlayerNorth().getInGamePieces().size()
		// + rootNode.getState().getPlayerSouth().getInGamePieces().size();

		visititedNode.setState(schuffleRoot(initGame));

    while (!rules.isTerminal(visititedNode.getState()) && !checkIfLeafNode(visititedNode)) {
			TreeNode<StrategoGame, StrategoAbstractEvent> selectChild = selection.selectChild(visititedNode);
      if (selectChild == null) {
        break;
      } else {
        visititedNode = selectChild;
      }
		}
		// Logger.println(" selection result: " + " depth " + visititedNode.getNodeDepth());

		// ssssS
		if (rules.isTerminal(visititedNode)) {
			int result = playthrough.returnStrategoPlaythroughResult(visititedNode.getState());
      result *= scoreMultiplier;
			updateTree(visititedNode, result);
			return;
		}
		ArrayList<StrategoAbstractEvent> moves = moveGenerator.generateAvailiableMoves(visititedNode.getState());

		addChildNodes(visititedNode, moves);
		visititedNode = selection.selectChild(visititedNode);
		int result = playthrough.returnStrategoPlaythroughResult(visititedNode.getState());
		if (visititedNode.getState().getActivePlayer() == 2) {
			result *= -1;
		}

		updateTree(visititedNode, result);


	}

	@Override
	public void addChildNodes(TreeNode<StrategoGame, StrategoAbstractEvent> aNode,
			ArrayList<StrategoAbstractEvent> moves) {

		for (int i = 0; i < moves.size(); i++) {
			@SuppressWarnings("unchecked")
			StrategoGame newState = (StrategoGame) aNode.getState().deepCopySelf();
			moveGenerator.applyAction(newState, moves.get(i));
			TreeNode<StrategoGame, StrategoAbstractEvent> newNode = new TreeNode<StrategoGame, StrategoAbstractEvent>(
					newState);
			newNode.setParent(aNode);
			aNode.getChildrenList().add(newNode);
			newNode.setNodeDepth(aNode.getNodeDepth() + 1);
			newNode.setAction(moves.get(i));
			// newNode.setPlaythoughNode(aNode.isPlaythoughNode());

		}
	}

	private StrategoGame schuffleRoot(StrategoGame initGame) {

		return initGame;
	}
	
}

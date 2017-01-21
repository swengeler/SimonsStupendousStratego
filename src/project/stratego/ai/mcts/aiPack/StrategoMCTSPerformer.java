package project.stratego.ai.mcts.aiPack;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.MCTSPerformer;
import project.stratego.ai.mcts.abstractSearchComponents.MoveGenerator;
import project.stratego.ai.mcts.abstractSearchComponents.Rules;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.logger.Logger;

public class StrategoMCTSPerformer extends MCTSPerformer<StrategoGame, StrategoAbstractEvent> {

	private StrategoPlaythrough playthrough;

	public StrategoMCTSPerformer(Rules<StrategoGame> rules,
			MoveGenerator<StrategoGame, StrategoAbstractEvent> moveGenerator, StrategoPlaythrough playthrough) {
		super(rules, moveGenerator, playthrough);
		this.playthrough = playthrough;
	}

	public TreeNode<StrategoGame, StrategoAbstractEvent> runMCTSMultiObvs(
			TreeNode<StrategoGame, StrategoAbstractEvent> rootNode) {

		for (int i = 0; i < noOfItterations; i++) {

			mctsItteration(rootNode);

			// Logger.println("number of  Tottal itterations  : " + (i + 1));
		}
		// Logger.println("" + rootNode.getGamesPlayed());

		Logger.println(rootNode.getState().toString());

		return getBestChild(rootNode);

	}

	@Override
	public void mctsItteration(TreeNode<StrategoGame, StrategoAbstractEvent> rootNode) {
		StrategoGame initGame = (StrategoGame) rootNode.getState().deepCopySelf();

		double scoreMultiplier = (initGame.getActivePlayer() == 1) ? 1.0 : -1.0;
		// int rootPieces = rootNode.getState().getPlayerNorth().getInGamePieces().size()
		// + rootNode.getState().getPlayerSouth().getInGamePieces().size();

		TreeNode<StrategoGame, StrategoAbstractEvent> visititedNode = rootNode;

		visititedNode.setState(schuffleRoot(initGame));

    while (!rules.isTerminal(visititedNode.getState()) && !checkIfLeafNode(visititedNode)) {
			TreeNode<StrategoGame, StrategoAbstractEvent> selectChild = selection.selectChild(visititedNode);
      if (selectChild == null) {
        break;
      } else {
        visititedNode = selectChild;
      }
		}
		Logger.println(" selection result: " + " depth " + visititedNode.getNodeDepth());


		if (rules.isTerminal(visititedNode)) {
			double result = playthrough.returnStrategoPlaythroughResult(visititedNode.getState());
			result *= scoreMultiplier;
			updateTree(visititedNode, result);
			return;
		}
		ArrayList<StrategoAbstractEvent> moves = moveGenerator.generateAvailiableMoves(visititedNode.getState());

		addChildNodes(visititedNode, moves);
		visititedNode = selection.selectChild(visititedNode);
		double result = playthrough.returnStrategoPlaythroughResult(visititedNode.getState());
		result *= scoreMultiplier;

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

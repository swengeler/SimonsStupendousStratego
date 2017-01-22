package project.stratego.ai.mcts.aiPack;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.MoveGenerator;
import project.stratego.ai.mcts.abstractSearchComponents.Playthrough;
import project.stratego.ai.mcts.abstractSearchComponents.Rules;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.control.managers.ModelComManager;

public class StrategoPlaythrough extends Playthrough<StrategoGame, StrategoAbstractEvent> {

	private MoveGenerator<StrategoGame, StrategoAbstractEvent> nodeGenerator;
	private Rules<StrategoGame> rules;

	public StrategoPlaythrough(MoveGenerator<StrategoGame, StrategoAbstractEvent> nodeGenerator,
			Rules<StrategoGame> rules) {
		super(nodeGenerator, rules);
		this.nodeGenerator = nodeGenerator;
		this.rules = rules;
	}



	public double returnStrategoPlaythroughResult(StrategoGame state) {
		state = (StrategoGame) state.deepCopySelf();

		// tempNode.setPlaythoughNode(true);

		// TreeNode<StrategoGame, StrategoAbstractEvent> rootNode = leafNode.getRootNode();
		// int referance = rootNode.getState().getActivePlayer();
		int movesCount = 0;
		while (!rules.isTerminal(state) && movesCount < 5) {
			state = (StrategoGame) state.deepCopySelf();

			ArrayList<StrategoAbstractEvent> childrenNodes = nodeGenerator.generateAvailiableMoves(state);
			try {
				nodeGenerator.applyAction(state, childrenNodes.get((int) (childrenNodes.size() * Math.random())));

			} catch (IndexOutOfBoundsException e) {
				for (int i = 0; i < 5; i++) {
					System.out.println();
				}
				ModelComManager.getInstance().printCurrentMatchUpBoard();
				for (int i = 0; i < 5; i++) {
					System.out.println();
				}
				e.printStackTrace();
			}
			movesCount++;

		}
		return rules.getScoreValue(state, 0);
	}


}

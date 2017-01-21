package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.*;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;

import java.util.ArrayList;

public class StrategoPlaythrough extends Playthrough<StrategoGame, StrategoAbstractEvent> {

	private MoveGenerator<StrategoGame, StrategoAbstractEvent> nodeGenerator;
	private Rules<StrategoGame> rules;

	public StrategoPlaythrough(MoveGenerator<StrategoGame, StrategoAbstractEvent> nodeGenerator, Rules<StrategoGame> rules) {
		super(nodeGenerator, rules);
		this.nodeGenerator = nodeGenerator;
		this.rules = rules;
	}

	public int returnStrategoPlaythroughResult(StrategoGame state) {
		state = (StrategoGame) state.deepCopySelf();

		// tempNode.setPlaythoughNode(true);

		// TreeNode<StrategoGame, StrategoAbstractEvent> rootNode = leafNode.getRootNode();
		// int referance = rootNode.getState().getActivePlayer();
		int movesCount = 0;
		while (!rules.isTerminal(state) && movesCount < 5) {
			state = (StrategoGame) state.deepCopySelf();

			ArrayList<StrategoAbstractEvent> childrenNodes = nodeGenerator.generateAvailiableMoves(state);
			nodeGenerator.applyAction(state, childrenNodes.get((int) (childrenNodes.size() * Math.random())));
			movesCount++;

		}
		return rules.getScoreValue(state, 0);
	}


}

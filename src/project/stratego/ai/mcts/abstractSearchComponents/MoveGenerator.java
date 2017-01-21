package project.stratego.ai.mcts.abstractSearchComponents;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.AbstractAction;
import project.stratego.ai.mcts.abstractDefinitions.SearchState;



public abstract class MoveGenerator<State extends SearchState, Action extends AbstractAction<State>> {
	
	
	
	public abstract ArrayList<Action> generateAvailiableMoves(State state);

	public abstract void applyAction(State state, Action action);
}



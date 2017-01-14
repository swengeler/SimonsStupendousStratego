package project.stratego.ai.mcts.abstractDefinitions;

public abstract class SearchState {

	public abstract SearchState deepCopySelf();

	public abstract int getActivePlayer();

}

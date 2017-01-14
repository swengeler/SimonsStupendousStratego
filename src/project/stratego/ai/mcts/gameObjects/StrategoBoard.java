package project.stratego.ai.mcts.gameObjects;


public class StrategoBoard {

	// private ArrayList<StrategoBoardTile> board = new ArrayList<StrategoBoardTile>();

	private StrategoBoardTile[][] board = new StrategoBoardTile[10][10];

	public StrategoBoardTile[][] getBoardStracture() {
		return board;
	}

}

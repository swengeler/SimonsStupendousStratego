package project.stratego.ai.mcts.factories;

import project.stratego.ai.mcts.gameObjects.*;

public class BoardFactory {

	private StrategoBoard tempBoard;

	public StrategoBoard createBoard() {

		StrategoBoard board = new StrategoBoard();
		tempBoard = board;

		for (int i = 0; i < board.getBoardStracture().length; i++) {
			for (int j = 0; j < board.getBoardStracture()[0].length; j++) {
				if (checkIfLake(i, j)) {
					addTile(i, j, TerrainType.GRASS);
				} else {
					addTile(i, j, TerrainType.LAKE);
				}

			}
		}

		return board;
	}

	private boolean checkIfLake(int i, int j) {
		return !((i == 4 && j == 2) || (i == 4 && j == 3) || (i == 5 && j == 2) || (i == 5 && j == 3)
				|| (i == 4 && j == 7) || (i == 4 && j == 6) || (i == 5 && j == 7) || (i == 5 && j == 6));
	}

	private void addTile(int yDim, int xDim, TerrainType terrainType) {

		tempBoard.getBoardStracture()[yDim][xDim] = new StrategoBoardTile(terrainType);

	}

}

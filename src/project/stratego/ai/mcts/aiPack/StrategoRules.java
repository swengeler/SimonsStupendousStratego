package project.stratego.ai.mcts.aiPack;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.Rules;
import project.stratego.ai.mcts.factories.GamePieceFactory;
import project.stratego.ai.mcts.gameLogic.SystemsManager;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;
import project.stratego.ai.mcts.logger.Logger;

public class StrategoRules extends Rules<StrategoGame> {

	// private SystemsManager systemManager;

	public StrategoRules(SystemsManager systemManager){
		// this.systemManager = systemManager;
	}

	@Override
	public boolean isTerminal(TreeNode<StrategoGame, ?> aNode) {
		StrategoGame game = aNode.getState();

		if (isTerminal(game)) {
			return true;
		}

		if (aNode.getChildrenList().size() == 0) {
			return true;
		}

		return false;
	}

	public double getScoreValue(StrategoGame state) {

		ArrayList<StrategoPiece> playerNorthPieces = state.getPlayerNorth().getInGamePieces();
		ArrayList<StrategoPiece> playerSouthPieces = state.getPlayerSouth().getInGamePieces();
		int tottalNorthStr = calculateTottalStr(playerNorthPieces);
		int tottalSouthStr = calculateTottalStr(playerSouthPieces);
		double difference = 1.0 * (tottalNorthStr - tottalSouthStr);
		// if (isTerminal(state)) {
		// double terminalScore = (difference > 0) ? 1.0 : -1.0;
		// return terminalScore;
		//
		// }
		// if (tottalNorthStr > tottalSouthStr) {
		// return 1;
		// }
		// if (tottalNorthStr < tottalSouthStr) {
		// return -1;
		// }
		// Logger.println("north str" + (tottalNorthStr));
		// Logger.println("north str" + (tottalNorthStr));
		// Logger.println("tottlStr" + (calculateTottalStr()));

		double score = 1.0 * (difference) / calculateTottalStr();
		if (score < -1.0 || score > 1.0)
			Logger.println("LAAAAAARGE trouble " + score);
		return score;
	}


	public boolean isTerminal(StrategoGame aState) {
		if (!checkForFlag(aState.getPlayerNorth().getInGamePieces())) {
			return true;
		}
		if (!checkForFlag(aState.getPlayerSouth().getInGamePieces())) {
			return true;
		}
		// TODO non-movement conditions

		return false;

	}


	private boolean checkForFlag(ArrayList<StrategoPiece> pieceList) {
		for (int i = 0; i < pieceList.size(); i++) {
			if (pieceList.get(i).getPieceType() == PieceType.FLAG) {
				return true;
			}
		}
		return false;
	}

	private int calculateTottalStr(ArrayList<StrategoPiece> pieceList) {
		int sum=0;
		for (int i = 0; i < pieceList.size(); i++) {
			StrategoPiece piece = pieceList.get(i);
			// if (piece.getPieceType() == PieceType.FLAG) {
			// sum += 1000;
			// } else {
				sum = sum + StrategoEvaluationValues.pieceValues.get(piece.getPieceType());
			// }
		}
		return sum;
	}

	@Override
	public int getScoreValue(TreeNode<StrategoGame, ?> aNode, int referance) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getScoreValue(StrategoGame state, int referance) {
		// TODO Review
		return getScoreValue(state);
	}

	public int calculateTottalStr() {
		int result = 0;
		GamePieceFactory factory = new GamePieceFactory();
		ArrayList<StrategoPiece> pieceList = factory.createPlayerPieces();
		for (int i = 0; i < pieceList.size(); i++) {
			result = result + StrategoEvaluationValues.pieceValues.get((pieceList.get(i).getPieceType()));
		}
		return result;

	}

}

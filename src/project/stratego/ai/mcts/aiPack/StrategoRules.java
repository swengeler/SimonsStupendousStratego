package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.Rules;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.gameLogic.SystemsManager;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

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

		return false;
	}

	public int getScoreValue(StrategoGame state) {
		if (isTerminal(state)) {
			// TODO Return high score for winner
		}
		ArrayList<StrategoPiece> playerNorthPieces = state.getPlayerNorth().getInGamePieces();
		ArrayList<StrategoPiece> playerSouthPieces = state.getPlayerSouth().getInGamePieces();
		int tottalNorthStr = calculateTottalStr(playerNorthPieces);
		int tottalSouthStr = calculateTottalStr(playerSouthPieces);
		if (tottalNorthStr > tottalSouthStr) {
			return 1;
		}
		if (tottalNorthStr < tottalSouthStr) {
			return -1;
		}


		return 0;
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

	public boolean isAttackAction(StrategoGame state, StrategoAbstractEvent action) {
		// TODO
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
      if (piece.getPieceType() == PieceType.FLAG) {
        sum += 1000;
      } else {
				// sum = sum + PieceHierarchyData.pieceLvlMap.get(piece.getPieceType());
      }
		}
		return sum;
	}

	@Override
	public int getScoreValue(TreeNode<StrategoGame, ?> aNode, int referance) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScoreValue(StrategoGame state, int referance) {
		// TODO Review
		return getScoreValue(state);
	}

}

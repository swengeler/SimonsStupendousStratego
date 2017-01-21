package project.stratego.ai.mcts.aiPack;

import java.util.HashMap;

import project.stratego.ai.mcts.gameObjects.PieceType;

public class StrategoEvaluationValues {
	public static final HashMap<PieceType, Integer> pieceValues = new HashMap<PieceType, Integer>();
	static {
		pieceValues.put(PieceType.BOMB, 10);
		pieceValues.put(PieceType.MARSHAL, 300);
		pieceValues.put(PieceType.GENERAL, 100);
		pieceValues.put(PieceType.COLONEL, 50);
		pieceValues.put(PieceType.MAJOR, 25);
		pieceValues.put(PieceType.CAPTAIN, 20);
		pieceValues.put(PieceType.LIEUTENANT, 10);
		pieceValues.put(PieceType.SERGEANT, 7);
		pieceValues.put(PieceType.MINER, 5);
		pieceValues.put(PieceType.SCOUT, 1);
		pieceValues.put(PieceType.SPY, 6);
		pieceValues.put(PieceType.FLAG, 10000);

	}


}

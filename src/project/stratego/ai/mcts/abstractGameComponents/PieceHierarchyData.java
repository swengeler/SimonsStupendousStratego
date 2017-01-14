package project.stratego.ai.mcts.abstractGameComponents;

import project.stratego.ai.mcts.gameObjects.PieceType;

import java.util.HashMap;

public class PieceHierarchyData {

	public static final HashMap<PieceType, Integer> pieceLvlMap = new HashMap<>();
	static {
		pieceLvlMap.put(PieceType.BOMB, 11);
		pieceLvlMap.put(PieceType.MARSHAL, 10);
		pieceLvlMap.put(PieceType.GENERAL, 9);
		pieceLvlMap.put(PieceType.COLONEL, 8);
		pieceLvlMap.put(PieceType.MAJOR, 7);
		pieceLvlMap.put(PieceType.CAPTAIN, 6);
		pieceLvlMap.put(PieceType.LIEUTENANT, 5);
		pieceLvlMap.put(PieceType.SERGEANT, 4);
		pieceLvlMap.put(PieceType.MINER, 3);
		pieceLvlMap.put(PieceType.SCOUT, 2);
		pieceLvlMap.put(PieceType.SPY, 1);
		pieceLvlMap.put(PieceType.FLAG, 0);

	}

	public static final HashMap<Integer, PieceType> pieceTypeMap = new HashMap<>();
	static {
		pieceTypeMap.put(11, PieceType.BOMB);
		pieceTypeMap.put(10, PieceType.MARSHAL);
		pieceTypeMap.put(9, PieceType.GENERAL);
		pieceTypeMap.put(8, PieceType.COLONEL);
		pieceTypeMap.put(7, PieceType.MAJOR);
		pieceTypeMap.put(6, PieceType.CAPTAIN);
		pieceTypeMap.put(5, PieceType.LIEUTENANT);
		pieceTypeMap.put(4, PieceType.SERGEANT);
		pieceTypeMap.put(3, PieceType.MINER);
		pieceTypeMap.put(2, PieceType.SCOUT);
		pieceTypeMap.put(1, PieceType.SPY);
		pieceTypeMap.put(0, PieceType.FLAG);

	}

}

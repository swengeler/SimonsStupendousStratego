package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.gameObjects.PieceType;

import java.util.HashMap;


/**
 * Created by Alexios on 1/17/2017.
 */
public class StrategoEvaluationValues {
    public static final HashMap<PieceType, Integer> pieceValues = new HashMap<PieceType, Integer>();
    static {
        pieceValues.put(PieceType.BOMB, 6);
        pieceValues.put(PieceType.MARSHAL, 12);
        pieceValues.put(PieceType.GENERAL, 10);
        pieceValues.put(PieceType.COLONEL, 9);
        pieceValues.put(PieceType.MAJOR, 8);
        pieceValues.put(PieceType.CAPTAIN, 7);

        pieceValues.put(PieceType.MINER, 4);
        pieceValues.put(PieceType.LIEUTENANT, 6);
        pieceValues.put(PieceType.SERGEANT, 5);
        pieceValues.put(PieceType.SCOUT, 1);
        pieceValues.put(PieceType.SPY, 6);
        pieceValues.put(PieceType.FLAG, 100);

    }

}

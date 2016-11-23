package project.stratego.game.utils;

import java.util.HashMap;

public enum PieceType {

    FLAG, BOMB, SPY, SCOUT, MINER, SERGEANT, LIEUTENANT, CAPTAIN, MAJOR, COLONEL, GENERAL, MARSHAL, UNKNOWN, MOVEKNOWN;

    public static final int[] pieceQuantity = {1, 6, 1, 8, 5, 4, 4, 4, 3, 2, 1, 1};

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

}

package project.stratego.game.moves;

import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

public class DiscreteMoveManager extends MoveManager {


    public DiscreteMoveManager(BoardTile[][] board) {
        super(board);
    }

    @Override
    protected boolean checkIfAttackWins(Piece attackingPiece, Piece defendingPiece) {
        if (attackingPiece.getType() == PieceType.SPY && defendingPiece.getType() == PieceType.MARSHAL) {
            return true;
        } else if (attackingPiece.getType() == PieceType.MARSHAL && defendingPiece.getType() == PieceType.SPY) {
            return true;
        }
        if (attackingPiece.getType() == PieceType.MINER && defendingPiece.getType() == PieceType.BOMB) {
            return true;
        }
        return (PieceType.pieceLvlMap.get(attackingPiece.getType())) > (PieceType.pieceLvlMap.get(defendingPiece.getType()));
    }

    @Override
    protected boolean checkIfDraw(Piece attackingPiece, Piece defendingPiece) {
        return attackingPiece.getType() == defendingPiece.getType();
    }

}
